package net.runelite.client.plugins.mafhamtob.Xarpus;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.mafhamtob.Direction;
import net.runelite.client.plugins.mafhamtob.MafhamToBConfig;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;

import javax.inject.Inject;
import java.awt.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class XarpusOverlay extends Overlay {

    @Inject
    private Client client;
    @Inject
    private MafhamToBConfig config;
    @Inject
    private Xarpus xarpus;

    private static final Function<WorldPoint, Point[]> getNEBoxPoints = (p) -> new Point[]{new Point(p.getX(), p.getY()), new Point(p.getX(), p.getY() + 8), new Point(p.getX() + 8, p.getY() + 8), new Point(p.getX() + 8, p.getY())};
    private static final Function<WorldPoint, Point[]> getNWBoxPoints = (p) -> new Point[]{new Point(p.getX() - 8, p.getY()), new Point(p.getX() - 8, p.getY() + 8), new Point(p.getX(), p.getY() + 8), new Point(p.getX(), p.getY())};
    private static final Function<WorldPoint, Point[]> getSEBoxPoints = (p) -> new Point[]{new Point(p.getX(), p.getY() - 8), new Point(p.getX(), p.getY()), new Point(p.getX() + 8, p.getY()), new Point(p.getX() + 8, p.getY() - 8)};
    private static final Function<WorldPoint, Point[]> getSWBoxPoints = (p) -> new Point[]{new Point(p.getX() - 8, p.getY() - 8), new Point(p.getX() - 8, p.getY()), new Point(p.getX(), p.getY()), new Point(p.getX(), p.getY() - 8)};
    private static final Function<WorldPoint, Point[]> getNEMeleePoints = (p) -> new Point[]{new Point(p.getX() + 4, p.getY() + 4), new Point(p.getX(), p.getY() + 4), new Point(p.getX(), p.getY() + 3), new Point(p.getX() + 3, p.getY() + 3), new Point(p.getX() + 3, p.getY()), new Point(p.getX() + 4, p.getY())};
    private static final Function<WorldPoint, Point[]> getNWMeleePoints = (p) -> new Point[]{new Point(p.getX() - 4, p.getY() + 4), new Point(p.getX() - 4, p.getY()), new Point(p.getX() - 3, p.getY()), new Point(p.getX() - 3, p.getY() + 3), new Point(p.getX(), p.getY() + 3), new Point(p.getX(), p.getY() + 4)};
    private static final Function<WorldPoint, Point[]> getSEMeleePoints = (p) -> new Point[]{new Point(p.getX() + 4, p.getY() - 4), new Point(p.getX() + 4, p.getY()), new Point(p.getX() + 3, p.getY()), new Point(p.getX() + 3, p.getY() - 3), new Point(p.getX(), p.getY() - 3), new Point(p.getX(), p.getY() - 4)};
    private static final Function<WorldPoint, Point[]> getSWMeleePoints = (p) -> new Point[]{new Point(p.getX() - 4, p.getY() - 4), new Point(p.getX(), p.getY() - 4), new Point(p.getX(), p.getY() - 3), new Point(p.getX() - 3, p.getY() - 3), new Point(p.getX() - 3, p.getY()), new Point(p.getX() - 4, p.getY())};

    @Inject
    private XarpusOverlay(Client client, MafhamToBConfig config, Xarpus xarpus)
    {
        this.client = client;
        this.xarpus = xarpus;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (xarpus.getXarpus_NPC() == null)
        {
            return null;
        }
        if (xarpus.getExhumedCounter() != null)
        {
            String string = String.valueOf(xarpus.getExhumedCounter());
            Point canvasPoint = xarpus.getXarpus_NPC().getCanvasTextLocation(graphics, string, 150);
            renderTextLocation(graphics, string, 12, Font.BOLD, Color.WHITE, canvasPoint);
        }
        renderLineOfSightPolygon(graphics);
        NPC boss = xarpus.getXarpus_NPC();

        if (boss.getId() == NpcID.XARPUS_8340 || boss.getId() == NpcID.XARPUS_10772 || boss.getId() == NpcID.XARPUS_10768) //phase 2
        {
            int tick = xarpus.getXarpus_TicksUntilShoot();
            final String ticksLeftStr = String.valueOf(tick);
            Point canvasPoint = boss.getCanvasTextLocation(graphics, ticksLeftStr, 130);
            net.runelite.api.Point piePoint = Perspective.localToCanvas(client, xarpus.getXarpus_NPC().getLocalLocation(), client.getPlane(), 130);
            if (config.xarpusCounterMode() == MafhamToBConfig.XARPUS_TIMER_STYLE.PIE && !xarpus.isXarpus_Stare() && tick < 4)
            {
                tick = tick - 1;
                drawPie(piePoint, graphics, tick, 3);
            }
            if (config.xarpusCounterMode() == MafhamToBConfig.XARPUS_TIMER_STYLE.PIE && xarpus.isXarpus_Stare())
            {
                //drawPie(piePoint, graphics, tick, 8);
            }
            if (config.xarpusCounterMode() == MafhamToBConfig.XARPUS_TIMER_STYLE.NUMBER)
            {
                renderTextLocation(graphics, ticksLeftStr, 12, Font.BOLD, Color.WHITE, canvasPoint);
            }
            if (xarpus.isXarpus_Stare())
            {
                renderTextLocation(graphics, ticksLeftStr, 12, Font.BOLD, Color.WHITE, canvasPoint);
            }
        }
        if (boss.getId() == NpcID.XARPUS_8339 || boss.getId() == NpcID.XARPUS_10771 || boss.getId() == NpcID.XARPUS_10767) //phase 1
        {
            for (Map.Entry<GroundObject, Integer> entry : xarpus.getXarpus_Exhumeds().entrySet())
            {
                GroundObject o = entry.getKey();
                String string = entry.getValue().toString();
                Point canvasPoint = o.getCanvasTextLocation(graphics, string, 0);
                Polygon poly = o.getCanvasTilePoly();
                if (poly != null)
                {
                    graphics.setColor(new Color(0, 255, 0, 130));
                    graphics.setStroke(new BasicStroke(1));
                    graphics.draw(poly);
                }
                OverlayUtil.renderTextLocation(graphics, canvasPoint, string, Color.cyan);
            }
        }
        return null;
    }

    private void renderTextLocation(Graphics2D graphics, String txtString, int fontSize, int fontStyle, Color fontColor, Point canvasPoint)
    {
        graphics.setFont(new Font("Arial", fontStyle, fontSize));
        if (canvasPoint != null)
        {
            final Point canvasCenterPoint = new Point(
                    canvasPoint.getX(),
                    canvasPoint.getY());
            final Point canvasCenterPoint_shadow = new Point(
                    canvasPoint.getX() + 1,
                    canvasPoint.getY() + 1) ;
            OverlayUtil.renderTextLocation(graphics, canvasCenterPoint_shadow, txtString, Color.BLACK);
            OverlayUtil.renderTextLocation(graphics, canvasCenterPoint, txtString, fontColor);
        }
    }

    private void drawPie(Point canvasPoint, Graphics2D graphics2D, int timer, int denominator) {
        double progress = (double) timer / denominator;
        if (progress < 0)
        {
            return;
        }
        ProgressPieComponent pie = new ProgressPieComponent();
        Color color = progress == 0 ? Color.green : Color.cyan;
        pie.setPosition(canvasPoint);
        pie.setBorderColor(color);
        pie.setFill(color);
        pie.setProgress(1 - progress);

        pie.render(graphics2D);
    }

    private void renderLineOfSightPolygon(Graphics2D graphics)
    {
        NPC xarpusNpc = xarpus.getXarpus_NPC();
        if (xarpusNpc != null && (xarpusNpc.getId() == NpcID.XARPUS_8340 || xarpusNpc.getId() == 10768 || xarpusNpc.getId() == 10772) && !xarpusNpc.isDead() && xarpus.isXarpus_Stare_Dangerous())
        {
            if (!config.stareTiles())
            {
                return;
            }
            WorldPoint xarpusWorldPoint = WorldPoint.fromLocal(client, xarpusNpc.getLocalLocation());
            Direction dir = Direction.getPreciseDirection(xarpusNpc.getOrientation());
            if (dir != null)
            {
                Point[] points;
                boolean markMeleeTiles = config.stareTiles();
                switch (dir)
                {
                    case NORTHEAST:
                        points = markMeleeTiles ? getNEMeleePoints.apply(xarpusWorldPoint) : getNEBoxPoints.apply(xarpusWorldPoint);
                        break;
                    case NORTHWEST:
                        points = markMeleeTiles ? getNWMeleePoints.apply(xarpusWorldPoint) : getNWBoxPoints.apply(xarpusWorldPoint);
                        break;
                    case SOUTHEAST:
                        points = markMeleeTiles ? getSEMeleePoints.apply(xarpusWorldPoint) : getSEBoxPoints.apply(xarpusWorldPoint);
                        break;
                    case SOUTHWEST:
                        points = markMeleeTiles ? getSWMeleePoints.apply(xarpusWorldPoint) : getSWBoxPoints.apply(xarpusWorldPoint);
                        break;
                    default:
                        return;
                }

                Polygon poly = new Polygon();
                Point[] dangerousPolygonPoints = points;
                int dangerousPolygonPointsLength = points.length;

                Arrays.stream(dangerousPolygonPoints, 0, dangerousPolygonPointsLength)
                        .map(point -> localToCanvas(dir, point.getX(), point.getY()))
                        .filter(Objects::nonNull)
                        .forEach(p -> poly.addPoint(p.getX(), p.getY()));

                graphics.setColor(new Color(255, 0 ,0, 125));
                graphics.fill(poly);
            }
        }
    }

    private Point localToCanvas(Direction dir, int px, int py)
    {
        LocalPoint lp = LocalPoint.fromWorld(client, px, py);
        int x = lp.getX();
        int y = lp.getY();
        int s = 64;
        switch (dir)
        {
            case NORTHEAST:
                return Perspective.localToCanvas(client, new LocalPoint(x - s, y - s), client.getPlane());
            case NORTHWEST:
                return Perspective.localToCanvas(client, new LocalPoint(x + s, y - s), client.getPlane());
            case SOUTHEAST:
                return Perspective.localToCanvas(client, new LocalPoint(x - s, y + s), client.getPlane());
            case SOUTHWEST:
                return Perspective.localToCanvas(client, new LocalPoint(x + s, y + s), client.getPlane());
            default:
                return null;
        }
    }
}