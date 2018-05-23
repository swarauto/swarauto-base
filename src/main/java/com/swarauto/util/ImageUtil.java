package com.swarauto.util;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_imgproc;

import java.io.File;
import java.nio.DoubleBuffer;

import static org.bytedeco.javacpp.opencv_core.CV_32FC1;
import static org.bytedeco.javacpp.opencv_core.minMaxLoc;
import static org.bytedeco.javacpp.opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.matchTemplate;

public class ImageUtil {
    /**
     * Check if the source file contains the template file. In case of matching then the location and
     * size will be returned, otherwise <code>null</code> as mismatch.
     *
     * @param sourceFilePath   full path of the source file which contain the game screen.
     * @param templateFilePath full path of the template file which contain a small part of game
     *                         screen e.g. a text or unique identifier of game screen.
     * @param threshold        percentage matching in order to consider as match e.g. 95% will be considered
     *                         as match
     * @return the location in pixel and size of the template file in source file, <code>null</code>
     * if not match in term of similarity and threshold
     */
    public static Rectangle contains(final String sourceFilePath, final String templateFilePath,
                                     final double threshold) {
        Mat sourceGrey = loadImageInGrey(sourceFilePath);
        if (sourceGrey == null) return null;

        Rectangle rectangle = contains(sourceGrey, templateFilePath, threshold);
        sourceGrey.release();

        return rectangle;
    }

    public static Rectangle contains(final Mat sourceGrey, final String templateFilePath,
                                     final double threshold) {
        Mat templateGrey = loadImageInGrey(templateFilePath);
        if (templateGrey == null) return null;

        Rectangle rectangle = contains(sourceGrey, templateGrey, threshold);
        templateGrey.release();

        return rectangle;
    }

    public static Rectangle contains(final Mat sourceGrey, final Mat templateGrey,
                                     final double threshold) {
        Mat result = null;
        try {
            final Size size = new Size(sourceGrey.cols() - templateGrey.cols() + 1,
                    sourceGrey.rows() - templateGrey.rows() + 1);
            result = new Mat(size, CV_32FC1);
            matchTemplate(sourceGrey, templateGrey, result, opencv_imgproc.TM_CCORR_NORMED);

            final DoubleBuffer minVal = DoubleBuffer.allocate(8);
            final DoubleBuffer maxVal = DoubleBuffer.allocate(8);
            final Point minLoc = new Point();
            final Point maxLoc = new Point();
            minMaxLoc(result, minVal, maxVal, minLoc, maxLoc, null);

            final double similarity = maxVal.get() * 100;

            return similarity >= threshold
                    ? new Rectangle(maxLoc.x(), maxLoc.y(), templateGrey.cols(), templateGrey.rows())
                    : null;
        } catch (Exception e) {
            return null;
        } finally {
            if (result != null) result.release();
        }
    }

    public static Mat loadImageInGrey(String imagePath) {
        if (imagePath == null || !new File(imagePath).exists()) return null;

        Mat image = null;
        try {
            image = imread(imagePath, CV_LOAD_IMAGE_GRAYSCALE);
        } catch (Exception e) {
            if (image != null) image.release();
            return null;
        }

        return image;
    }
}
