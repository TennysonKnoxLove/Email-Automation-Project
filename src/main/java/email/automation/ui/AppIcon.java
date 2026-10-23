package email.automation.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public final class AppIcon {
    private static final int[] SIZES = { 16, 32, 48, 64, 128, 256, 512 };

    private AppIcon() {}

    public static List<BufferedImage> images() {
        List<BufferedImage> images = new ArrayList<>();
        for (int size : SIZES) {
            images.add(create(size));
        }
        return images;
    }

    public static BufferedImage create(int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        g.setColor(new Color(28, 36, 48));
        g.fillRect(0, 0, size, size);

        double s = size;
        double letterX = s * 0.28;
        double letterY = s * 0.16;
        double letterW = s * 0.44;
        double letterH = s * 0.36;
        g.setColor(new Color(255, 251, 245));
        g.fill(new RoundRectangle2D.Double(letterX, letterY, letterW, letterH, s * 0.035, s * 0.035));

        double fold = s * 0.075;
        Path2D corner = new Path2D.Double();
        corner.moveTo(letterX + letterW - fold, letterY);
        corner.lineTo(letterX + letterW, letterY + fold);
        corner.lineTo(letterX + letterW - fold, letterY + fold);
        corner.closePath();
        g.setColor(new Color(229, 214, 194));
        g.fill(corner);

        if (size >= 48) {
            g.setColor(new Color(201, 184, 160));
            g.setStroke(new BasicStroke(Math.max(1.5f, (float) (s * 0.012)), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            float left = (float) (letterX + s * 0.07);
            float right = (float) (letterX + letterW - s * 0.09);
            for (int i = 0; i < 3; i++) {
                float y = (float) (letterY + s * 0.14 + i * s * 0.055);
                float end = i == 2 ? left + (right - left) * 0.55f : right;
                g.drawLine(Math.round(left), Math.round(y), Math.round(end), Math.round(y));
            }
        }

        double bodyX = s * 0.14;
        double bodyY = s * 0.42;
        double bodyW = s * 0.72;
        double bodyH = s * 0.40;
        g.setColor(new Color(246, 239, 226));
        g.fill(new RoundRectangle2D.Double(bodyX, bodyY, bodyW, bodyH, s * 0.045, s * 0.045));

        Path2D flap = new Path2D.Double();
        flap.moveTo(bodyX + s * 0.015, bodyY + s * 0.02);
        flap.lineTo(bodyX + bodyW / 2.0, bodyY + bodyH * 0.58);
        flap.lineTo(bodyX + bodyW - s * 0.015, bodyY + s * 0.02);
        flap.closePath();
        g.setColor(new Color(232, 214, 186));
        g.fill(flap);
        g.setColor(new Color(186, 156, 116));
        g.setStroke(new BasicStroke(Math.max(1.25f, (float) (s * 0.012)), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(flap);

        g.dispose();
        return image;
    }
}
