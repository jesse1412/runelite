package net.runelite.client.plugins.mafhamtoa.KephriPuzzle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.coords.LocalPoint;

import java.awt.*;
@Getter
@Setter
@AllArgsConstructor
public class PuzzleTile {
    private String name;
    private Color color;
    private LocalPoint localPoint;

}