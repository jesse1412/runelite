package net.runelite.client.plugins.demonicgorilla;

import java.util.function.Predicate;
import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.coords.LocalPoint;

public class GorillaUtils
{
    /**
     * Calculates the next area that will be occupied if this area attempts
     * to move toward a target by using the normal NPC traveling pattern.
     *
     * @param client The client instance to calculate with.
     * @param source The current area (WorldArea) representing the entity.
     * @param target The target area the entity wants to move toward.
     * @param stopAtMeleeDistance Whether to stop at melee distance from the target.
     * @param extraCondition An additional condition to perform when checking valid tiles,
     *                       such as performing a check for un-passable actors.
     * @return The next area (WorldArea) to be occupied.
     */
    public static WorldArea calculateNextTravellingPoint(Client client, WorldArea source, WorldArea target,
                                                         boolean stopAtMeleeDistance, Predicate<? super WorldPoint> extraCondition)
    {
        if (source.getPlane() != target.getPlane())
        {
            return null;
        }

        if (source.intersectsWith(target))
        {
            if (stopAtMeleeDistance)
            {
                // Movement is unpredictable when the NPC and actor stand on top of each other
                return null;
            }
            else
            {
                return source;
            }
        }

        // Calculate distances along each axis (manual replacement for getAxisDistances)
        int deltaX = Math.abs(target.getX() - source.getX());
        int deltaY = Math.abs(target.getY() - source.getY());

        int dx = target.getX() - source.getX();
        int dy = target.getY() - source.getY();

        if (stopAtMeleeDistance && source.isInMeleeDistance(target))
        {
            // NPC is in melee distance of the target, so no movement is done
            return source;
        }

        LocalPoint lp = LocalPoint.fromWorld(client, source.getX(), source.getY());
        if (lp == null ||
                lp.getSceneX() + dx < 0 || lp.getSceneX() + dx >= Constants.SCENE_SIZE ||
                lp.getSceneY() + dy < 0 || lp.getSceneY() + dy >= Constants.SCENE_SIZE)
        {
            // NPC is traveling out of the scene, so collision data isn't available
            return null;
        }

        int dxSig = Integer.signum(dx);
        int dySig = Integer.signum(dy);

        if (stopAtMeleeDistance && deltaX == 1 && deltaY == 1)
        {
            // Stop at melee distance when the entity is diagonally positioned relative to the target
            if (source.canTravelInDirection(client.getTopLevelWorldView(), dxSig, 0, extraCondition))
            {
                return new WorldArea(source.getX() + dxSig, source.getY(), source.getWidth(), source.getHeight(), source.getPlane());
            }
        }
        else
        {
            // Attempt to travel diagonally first
            if (source.canTravelInDirection(client.getTopLevelWorldView(), dxSig, dySig, extraCondition))
            {
                return new WorldArea(source.getX() + dxSig, source.getY() + dySig, source.getWidth(), source.getHeight(), source.getPlane());
            }
            // Try moving horizontally
            else if (dx != 0 && source.canTravelInDirection(client.getTopLevelWorldView(), dxSig, 0, extraCondition))
            {
                return new WorldArea(source.getX() + dxSig, source.getY(), source.getWidth(), source.getHeight(), source.getPlane());
            }
            // Try moving vertically
            else if (dy != 0 && Math.max(deltaX, deltaY) > 1 && source.canTravelInDirection(client.getTopLevelWorldView(), 0, dySig, extraCondition))
            {
                return new WorldArea(source.getX(), source.getY() + dySig, source.getWidth(), source.getHeight(), source.getPlane());
            }
        }

        // If no valid movement is found, return the current area (stuck)
        return source;
    }

    /**
     * Overload of calculateNextTravellingPoint without the extra condition.
     */
    public static WorldArea calculateNextTravellingPoint(Client client, WorldArea source, WorldArea target,
                                                         boolean stopAtMeleeDistance)
    {
        return calculateNextTravellingPoint(client, source, target, stopAtMeleeDistance, x -> true);
    }
}
