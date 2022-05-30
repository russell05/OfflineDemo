package com.example.bdtrack.Util;

import ohos.agp.components.element.Element;
import ohos.agp.components.element.PixelMapElement;
import ohos.app.Context;
import ohos.global.resource.*;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.media.image.common.PixelFormat;
import ohos.media.image.common.Size;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

public class ResUtil {
    private ResUtil() {}

    /**
     * get the path from id
     *
     * @param context the context
     * @param id      the id
     * @return the path from id
     */
    public static String getPathById(Context context, int id) {
        String path = "";
        if (context == null) {
            return path;
        }
        ResourceManager manager = context.getResourceManager();
        if (manager == null) {
            return path;
        }
        try {
            path = manager.getMediaPath(id);
        } catch (IOException | NotExistException | WrongTypeException e) {
            path = "";
        }
        return path;
    }

    /**
     * get the color
     *
     * @param context the context
     * @param id      the id
     * @return the color
     */
    public static int getColor(Context context, int id) {
        int result = 0;
        if (context == null) {
            return result;
        }
        ResourceManager manager = context.getResourceManager();
        if (manager == null) {
            return result;
        }
        try {
            result = manager.getElement(id).getColor();
        } catch (IOException | WrongTypeException | NotExistException e) {
            result = 0;
        }
        return result;
    }

    /**
     * get the pixel map
     *
     * @param context the context
     * @param id      the id
     * @return the pixel map
     */
    public static Optional<PixelMap> getPixelMap(Context context, int id) {
        String path = getPathById(context, id);
        if (path == null || path.length() == 0) {
            return Optional.empty();
        }
        RawFileEntry assetManager = context.getResourceManager().getRawFileEntry(path);
        ImageSource.SourceOptions options = new ImageSource.SourceOptions();
        options.formatHint = "image/png";
        ImageSource.DecodingOptions decodingOptions = new ImageSource.DecodingOptions();
        try {
            Resource asset = assetManager.openRawFile();
            ImageSource source = ImageSource.create(asset, options);
            return Optional.ofNullable(source.createPixelmap(decodingOptions));
        } catch (IOException e) {
            Optional.empty();
        }
        return Optional.empty();
    }

    /**
     * get Element from Resource
     *
     * @param resource the resource
     * @return Element
     * @throws IOException IOException
     * @throws NotExistException NotExistException
     */
    public static PixelMapElement prepareElement(Resource resource) throws IOException, NotExistException {
        return new PixelMapElement(preparePixelmap(resource));
    }

    /**
     * get PixelMap from Resource
     *
     * @param resource the resource
     * @return PixelMap
     * @throws IOException IOException
     * @throws NotExistException NotExistException
     */
    public static PixelMap preparePixelmap(Resource resource) throws IOException, NotExistException {
        ImageSource.SourceOptions srcOpts = new ImageSource.SourceOptions();
        ImageSource imageSource = null;
        try {
            imageSource = ImageSource.create(readResource(resource), srcOpts);
        } finally {
            close(resource);
        }
        if (imageSource == null) {
            throw new FileNotFoundException();
        }
        ImageSource.DecodingOptions decodingOpts = new ImageSource.DecodingOptions();
        decodingOpts.desiredSize = new Size(0, 0);
        decodingOpts.desiredRegion = new ohos.media.image.common.Rect(0, 0, 0, 0);
        decodingOpts.desiredPixelFormat = PixelFormat.ARGB_8888;

        PixelMap pixelmap = imageSource.createPixelmap(decodingOpts);
        return pixelmap;
    }

    /**
     * close Resource
     *
     * @param resource the resource
     */
    private static void close(Resource resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                return;
            }
        }
    }

    /**
     * get bytes from Resource
     *
     * @param resource the resource
     * @return bytes
     */
    private static byte[] readResource(Resource resource) {
        final int bufferSize = 1024;
        final int ioEnd = -1;

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[bufferSize];
        byte[] bytes = null;
        try {
            while (true) {
                int readLen = resource.read(buffer, 0, bufferSize);
                if (readLen == ioEnd) {
                    break;
                }
                output.write(buffer, 0, readLen);
            }
        } catch (IOException e) {
            return null;
        } finally {
            bytes = output.toByteArray();
            try {
                output.close();
            } catch (IOException e) {
                bytes = null;
            }
        }
        return bytes;
    }

    /**
     * get Element from ResourceId
     *
     * @param context the context
     * @param resId the resourceId
     * @return Element
     */
    public static Element getDrawable(Context context, int resId) {
        ResourceManager rsrc = context.getResourceManager();
        if (rsrc == null) {
            return null;
        }

        Element drawable = null;
        if (resId != 0) {
            try {
                Resource resource = rsrc.getResource(resId);
                drawable = (Element) prepareElement(resource);
            } catch (Exception e) {
                drawable = null;
            }
        }
        return drawable;
    }
}
