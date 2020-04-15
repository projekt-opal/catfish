package org.dice_research.opal.catfish.service.impl;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dice_research.opal.catfish.service.Cleanable;
import org.dice_research.opal.common.vocabulary.Opal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles data formats.
 *
 * @author Adrian Wilke
 */
public class FormatCleaner implements Cleanable {

    public final static int EXT_MAX_LENGTH = 12;
    public final static String PREFIX_IANA_VENDOR = "vnd.";
    public final static String RESOURCE_WHITELIST = "extensions-whiltelist.txt";
    public final static Pattern PATTERN_FINAL = Pattern
            .compile("[a-zA-Z][a-zA-Z0-9+-]{2," + (EXT_MAX_LENGTH - 1) + "}");
    public final static Pattern PATTERN_ZIP = Pattern.compile("^zip[ ]*\\(([a-zA-Z0-9+]{2,5})\\)");
    public final static Pattern PATTERN_BRACKET = Pattern
            .compile("^([a-zA-Z0-9+]{2,12})[ ]+\\(([\\.]*[a-zA-Z0-9+]{2,12})\\)");
    public final static Pattern PATTERN_EXTENSION = Pattern.compile("^[a-z][a-z0-9]{1," + (EXT_MAX_LENGTH - 1) + "}");
    public final static List<String> IANA_MIMETYPES;
    public final static List<String> EXTENSIONS_WHILTELIST;
    private static final Logger LOGGER = LogManager.getLogger();

    static {
        String[] mimeTypes = {"application", "audio", "font", "example", "image", "message", "model", "multipart",
                "text", "video"};
        IANA_MIMETYPES = Arrays.asList(mimeTypes);

        EXTENSIONS_WHILTELIST = new LinkedList<>();
        try {
            EXTENSIONS_WHILTELIST.addAll(readExtensionsWhitelist());
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    protected static List<String> readExtensionsWhitelist() throws IOException {
        List<String> list = new LinkedList<>();
        InputStream inputStream = FormatCleaner.class.getClassLoader().getResourceAsStream(RESOURCE_WHITELIST);
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line = bufferedReader.readLine();
            while (line != null) {
                list.add(line);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        }
        return list;
    }

    @Override
    public void clean(Model model) {
        List<Resource> dataSets = model.listResourcesWithProperty(RDF.type, DCAT.Dataset).toList();
        dataSets.forEach(dataSet -> {
            clean(model, dataSet);
        });
    }

    @Deprecated
    /**
     * This method is going to be a private method, use clean(Model model) instead
     */
    public void clean(Model model, Resource dataset) {
        NodeIterator nodeIterator;

        // Go through distributions

        NodeIterator distributionIt = model.listObjectsOfProperty(dataset, DCAT.distribution);
        while (distributionIt.hasNext()) {
            RDFNode rdfNode = distributionIt.next();
            if (!rdfNode.isResource()) {
                continue;
            }
            Resource distribution = rdfNode.asResource();

            Set<String> allFormats = new HashSet<>();

            // Media type

            nodeIterator = model.listObjectsOfProperty(distribution, DCAT.mediaType);
            while (nodeIterator.hasNext()) {
                RDFNode mediaType = nodeIterator.next();
                for (String value : getValues(mediaType, true)) {
                    allFormats.addAll(cleanInput(value));
                }
            }

            // Format

            nodeIterator = model.listObjectsOfProperty(distribution, DCTerms.format);
            while (nodeIterator.hasNext()) {
                RDFNode format = nodeIterator.next();
                for (String value : getValues(format, true)) {
                    allFormats.addAll(cleanInput(value));
                }
            }

            // Download URL

            if (allFormats.isEmpty()) {
                nodeIterator = model.listObjectsOfProperty(distribution, DCAT.downloadURL);
                while (nodeIterator.hasNext()) {
                    RDFNode downloadUrl = nodeIterator.next();
                    for (String value : getValues(downloadUrl, true)) {
                        String cleanedUrl = cleanDownloadUrl(value);
                        if (cleanedUrl != null) {
                            allFormats.add(cleanDownloadUrl(value));
                        }
                    }
                }
            }

            // Add formats to model

            for (String format : allFormats) {
                Resource formatResource = model.getResource(Opal.NS_OPAL_FORMAT + format);
                model.add(formatResource, RDF.type, Opal.OPAL_FORMAT);
                distribution.addProperty(DCTerms.format, formatResource);
            }

        }
    }

    /**
     * Returns URI of resources, literals as strings or null. If is blank node and
     * checkNextTriple is set, values of further triples are also checked.
     */
    protected Set<String> getValues(RDFNode rdfNode, boolean checkNextTriple) {
        Set<String> values = new HashSet<>();
        if (rdfNode.isURIResource()) {
            values.add(rdfNode.asResource().getURI());
        } else if (rdfNode.isLiteral()) {
            values.add(rdfNode.asLiteral().getString());
        } else if (rdfNode.isAnon() && checkNextTriple) {
            StmtIterator stmtIterator = rdfNode.asResource().listProperties();
            while (stmtIterator.hasNext()) {
                values.addAll(getValues(stmtIterator.next().getObject(), false));
            }
        }
        return values;
    }

    /**
     * Cleans any type of string.
     */
    public Set<String> cleanInput(String string) {
        Set<String> formats = new HashSet<>();

        // Empty values
        if (string == null || string.trim().isEmpty()) {
            return formats;
        }

        // Use only lower-case values for comparison
        string = string.toLowerCase().trim();

        // URLs
        if (string.startsWith("http")) {
            try {
                URL url = new URL(string);
                if (url.getPath().isEmpty()) {
                    // No path in URL -> no formats
                    return formats;
                }
                int index = url.getPath().lastIndexOf('/');
                if (index == -1) {
                    // No last path element found -> no formats
                    return formats;
                }
                string = url.getPath().substring(index + 1);
                if (string.isEmpty()) {
                    // No value -> no formats
                    return formats;
                }

                // Special HTML characters.
                // java.net.URLDecoder is not used, as URLs may contain '+'.
                string = string.replace("%20", " ");

            } catch (MalformedURLException e) {
                // Invalid URL -> no formats
                return formats;
            }
        }

        for (String cleanedValue : cleanValue(string)) {
            finalize(formats, cleanedValue);
        }

        return formats;
    }

    /**
     * Handles plain values (non-URLs).
     */
    protected Set<String> cleanValue(String value) {
        Set<String> values = new HashSet<>();
        String[] parts;

        // Recursively handle multiple values
        parts = value.split("[ ]*,[ ]*");
        if (parts.length > 1) {
            for (String part : parts) {
                values.addAll(cleanValue(part));
            }
            return values;
        }

        // Recursively handle brackets
        Matcher matcher = PATTERN_BRACKET.matcher(value);
        if (matcher.matches()) {
            values.addAll(cleanValue(matcher.group(1)));
            values.addAll(cleanValue(matcher.group(2)));
            return values;
        }

        // E.g. 'application/rdf+xml' -> 'rdf+xml'
        value = removeIanaMimeType(value);

        // E.g. 'vnd.geo+json' -> 'geo+json'
        value = removeIanaVendor(value);

        // E.g. '.xlsx' -> 'xlsx'
        if (value.startsWith(".")) {
            value = value.substring(1);
        }

        values.add(value);

        return values;
    }

    /**
     * Removes IANA vendor prefix.
     * <p>
     * E.g. 'vnd.geo+json' -> 'geo+json'.
     * <p>
     * E.g. 'vnd.google-earth.kml+xml' -> 'kml+xml'.
     *
     * @see https://www.iana.org/assignments/media-types/media-types.xhtml
     * @see https://tools.ietf.org/html/rfc2048#section-2.1
     */
    protected String removeIanaVendor(String value) {
        if (value.startsWith(PREFIX_IANA_VENDOR)) {
            value = value.substring(PREFIX_IANA_VENDOR.length());
            int index = value.lastIndexOf('.');
            if (index != -1) {
                value = value.substring(index + 1);
            }
        }
        return value;
    }

    /**
     * Removes IANA mime type information.
     * <p>
     * E.g. 'application/rdf+xml' -> 'rdf+xml'.
     *
     * @see https://www.iana.org/assignments/media-types/media-types.xhtml
     */
    protected String removeIanaMimeType(String value) {
        String[] parts = value.split("/");
        if (parts.length == 2) {
            if (IANA_MIMETYPES.contains(parts[0])) {
                return parts[1];
            }
        }
        return value;
    }

    /**
     * Final check.
     */
    protected void finalize(Set<String> formats, String value) {
        if (PATTERN_FINAL.matcher(value).matches()) {
            formats.add(value);
        }
    }

    /**
     * Checks URL for format type.
     *
     * @return extracted format type or null
     */
    public String cleanDownloadUrl(String urlString) {

        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            return null;
        }

        int index = url.getPath().lastIndexOf('.');
        if (index != -1) {
            Matcher matcher = PATTERN_EXTENSION.matcher(url.getPath().toLowerCase().substring(index + 1));
            if (matcher.matches()) {
                String extension = matcher.group();
                if (EXTENSIONS_WHILTELIST.contains(extension)) {
                    return extension;
                }
            }
        }

        return null;
    }
}