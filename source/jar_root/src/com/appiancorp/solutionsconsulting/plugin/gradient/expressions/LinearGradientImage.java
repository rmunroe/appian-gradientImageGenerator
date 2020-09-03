package com.appiancorp.solutionsconsulting.plugin.gradient.expressions;

import com.appiancorp.solutionsconsulting.plugin.gradient.helpers.ContentHelper;
import com.appiancorp.suiteapi.common.exceptions.AppianException;
import com.appiancorp.suiteapi.content.ContentOutputStream;
import com.appiancorp.suiteapi.content.ContentService;
import com.appiancorp.suiteapi.expression.annotations.AppianScriptingFunctionsCategory;
import com.appiancorp.suiteapi.expression.annotations.Function;
import com.appiancorp.suiteapi.expression.annotations.Parameter;
import com.appiancorp.suiteapi.knowledge.Document;
import com.appiancorp.suiteapi.knowledge.DocumentDataType;
import com.appiancorp.suiteapi.knowledge.FolderDataType;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

@AppianScriptingFunctionsCategory
public class LinearGradientImage {
    @Function
    public @DocumentDataType
    Long linearGradientImage(
            ContentService contentService,
            @Parameter String[] hexColors,
            @Parameter Double[] distribution,
            @Parameter Long width,
            @Parameter Long height,
            @Parameter Boolean horizontal,
            @Parameter @FolderDataType Long targetFolder
    ) throws IOException, AppianException {
        String fileExtension = "png";

        // Build filename from arguments
        StringBuilder sb = new StringBuilder("linear_");
        for (String hexColor : hexColors) {
            sb.append(hexColor.replaceAll("#", ""));
            sb.append("_");
        }
        for (Double dist : distribution) {
            sb.append(dist.toString());
            sb.append("_");
        }
        sb.append(width.toString());
        sb.append("_");
        sb.append(height.toString());
        if (horizontal)
            sb.append("_horizontal");
        else
            sb.append("_vertical");
        String filename = sb.toString();

        // Look for an existing file in the folder
        Long existingDocumentId = ContentHelper.findDocumentInFolder(contentService, filename, fileExtension, targetFolder);
        if (existingDocumentId != null) {
            return existingDocumentId;
        }

        // Convert hex colors to Colors
        Color[] colors = new Color[hexColors.length];
        for (int i = 0; i < hexColors.length; i++) {
            colors[i] = Color.decode(hexColors[i]);
        }

        // Convert doubles to floats
        float[] dist = new float[distribution.length];
        for (int i = 0; i < distribution.length; i++) {
            dist[i] = distribution[i].floatValue();
        }

        // Determine the start and end coordinates
        Point2D start = new Point2D.Double(0, 0);
        Point2D end = new Point2D.Double(0, height);
        if (horizontal)
            end = new Point2D.Double(width, 0);

        // Make a gradient
        LinearGradientPaint gradient = new LinearGradientPaint(start, end, dist, colors);

        // Create a new image
        BufferedImage gradientImage = new BufferedImage(width.intValue(), height.intValue(), BufferedImage.TYPE_INT_RGB);

        // Paint the image with gradient
        Graphics2D g2 = (Graphics2D) gradientImage.getGraphics();
        g2.setPaint(gradient);
        g2.fillRect(0, 0, width.intValue(), height.intValue());
        g2.dispose();

        // Save to the Folder
        Document newDocument = ContentHelper.createNewDocument(filename, "Created by the Gradient Image Generator plugin", targetFolder, fileExtension);
        ContentOutputStream cos = ContentHelper.uploadDocumentForWriting(contentService, newDocument);
        ImageIO.write(gradientImage, fileExtension, cos);
        cos.close();

        // Return the new Document ID
        return cos.getContentId();
    }
}