package net.runelite.client.plugins.mafhamcox.olmcounter;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;

@Slf4j
@Singleton
@Getter
@Setter
public class Olm
{
	public static final int HEAD_GAMEOBJECT_RISING = 29880;
	public static final int HEAD_GAMEOBJECT_READY = 29881;
	public static final int LEFT_HAND_GAMEOBJECT_RISING = 29883;
	public static final int LEFT_HAND_GAMEOBJECT_READY = 29884;
	public static final int RIGHT_HAND_GAMEOBJECT_RISING = 29886;
	public static final int RIGHT_HAND_GAMEOBJECT_READY = 29887;

	@Inject
	private Client client;

	public boolean active = false; // in fight
	public boolean firstPhase = false;
	public boolean finalPhase = false;
	public boolean interPhase = false;

	public boolean headPhase = false;
	public PhaseType phaseType = PhaseType.UNKNOWN;

	public GameObject hand = null;
	public OlmAnimation handAnimation = OlmAnimation.UNKNOWN;
	public GameObject head = null;
	public OlmAnimation headAnimation = OlmAnimation.UNKNOWN;

	public int ticksUntilNextAttack = -1;
	public int attackCycle = 1;
	public int specialCycle = 1;
	public int interPhaseTimer = -1;

	public boolean crippled = false;
	public int crippleTicks = 45;

	public int savedcripplecounter = 0;

	public Prayer prayer = null;
	public long lastPrayTime = 0;

	public void startPhase()
	{
		firstPhase = !active;
		active = true;
		ticksUntilNextAttack = -1;
		attackCycle = 1;
		specialCycle = 1;
		crippled = false;
		crippleTicks = 45;
		prayer = null;
		headAnimation = OlmAnimation.UNKNOWN;
		handAnimation = OlmAnimation.UNKNOWN;
	}

	public void hardRest()
	{
		active = false;
		firstPhase = false;
		finalPhase = false;
		headPhase = false;
		phaseType = PhaseType.UNKNOWN;
		interPhase = false;
		interPhaseTimer = -1;
		hand = null;
		head = null;
		headAnimation = OlmAnimation.UNKNOWN;
		handAnimation = OlmAnimation.UNKNOWN;
		ticksUntilNextAttack = -1;
		attackCycle = 1;
		specialCycle = 1;
		crippled = false;
		crippleTicks = 45;
		prayer = null;
	}

	void cripple()
	{
		crippled = true;
		crippleTicks = 45;
	}

	void uncripple()
	{
		crippled = false;
		crippleTicks = 45;
	}

	public void update()
	{
		incrementTickCycle();
		headAnimations();
		handAnimations();
		updateCrippleSticks();
		incrementInterPhaseCycle();
	}

	public void incrementInterPhaseCycle()
	{
		if (!interPhase)
		{
			return;
		}
		if (interPhaseTimer == -1)
		{
			interPhaseTimer = 33;
		}
		else
		{
			interPhaseTimer--;
		}
	}

	public void incrementTickCycle()
	{
		if (ticksUntilNextAttack == 1)
		{
			ticksUntilNextAttack = 4;
			incrementAttackCycle();
		}
		else if (ticksUntilNextAttack != -1)
		{
			ticksUntilNextAttack--;
		}
	}

	public void startShit()
	{
		ticksUntilNextAttack = firstPhase ? 2 : 5;
		attackCycle = 1;
		specialCycle = 1;
	}

	public void incrementAttackCycle()
	{
		if (attackCycle == 4)
		{
			attackCycle = 1;
			incrementSpecialCycle();
		}
		else
		{
			attackCycle++;
		}
	}

	public void incrementSpecialCycle()
	{
		if ((crippled))
		{
			return;
		}
		if ((specialCycle == 4 && !finalPhase))
		{
			specialCycle = 2;
		}
		else if ((specialCycle == 4 && finalPhase))
		{
			specialCycle = 1;
		}
		else
		{
			specialCycle++;
		}
	}

	public void decrementSpecialCycle()
	{
		if ((specialCycle == 2 && !finalPhase))
		{
			specialCycle = 4;
		}
		else if ((specialCycle == 1 && finalPhase))
		{
			specialCycle = 4;
		}
		else
		{
			specialCycle--;
		}
	}

	public void specialSync(OlmAnimation currentAnimation)
	{
		ticksUntilNextAttack = 4;
		attackCycle = 1;
		switch (currentAnimation)
		{
			case LEFT_HAND_CRYSTALS1:
			case LEFT_HAND_CRYSTALS2:
				specialCycle = 2;
				break;
			case LEFT_HAND_LIGHTNING1:
			case LEFT_HAND_LIGHTNING2:
				specialCycle = 3;
				break;
			case LEFT_HAND_PORTALS1:
			case LEFT_HAND_PORTALS2:
				specialCycle = 4;
// 				specialCycle = finalPhase ? 1 : 2; //old logic
				break;
			case LEFT_HAND_HEAL1:
			case LEFT_HAND_HEAL2:
				specialCycle = 1;
				break;
		}
	}

	void updateCrippleSticks()
	{
		if (!crippled)
		{
			return;
		}

		crippleTicks--;
		if (crippleTicks <= 0)
		{
			crippled = false;
			crippleTicks = 45;
		}
	}

	private void headAnimations()
	{
		if (head == null || head.getRenderable() == null)
		{
			return;
		}
		OlmAnimation currentAnimation = OlmAnimation.fromId(((DynamicObject) head.getRenderable()).getAnimation().getId());

		if (currentAnimation == headAnimation)
		{
			return;
		}

		switch (currentAnimation)
		{
//          old method for starting the cycle, bugged when olm is scuffed do not use
/*			case HEAD_RISING_2:
			case HEAD_ENRAGED_RISING_2:
				ticksUntilNextAttack = firstPhase ? 6 : 8;
				attackCycle = firstPhase ? 1 : 1;
				specialCycle = 1;
				break;
*/
			case HEAD_ENRAGED_LEFT:
			case HEAD_ENRAGED_MIDDLE:
			case HEAD_ENRAGED_RIGHT:
				finalPhase = true;
				break;
			case HEAD_MIDDLE_TO_LEFT: //resync cases
			case HEAD_LEFT_TO_RIGHT:
			case HEAD_RIGHT_TO_LEFT:
			case HEAD_RIGHT_TO_MIDDLE:
			case HEAD_LEFT_TO_MIDDLE:
			case HEAD_MIDDLE_TO_RIGHT:
			case HEAD_ENRAGED_MIDDLE_TO_RIGHT:
			case HEAD_ENRAGED_MIDDLE_TO_LEFT:
			case HEAD_ENRAGED_LEFT_TO_RIGHT:
			case HEAD_ENRAGED_RIGHT_TO_MIDDLE:
			case HEAD_ENRAGED_LEFT_TO_MIDDLE:
			case HEAD_ENRAGED_RIGHT_TO_LEFT:
				ticksUntilNextAttack = 4;
				break;
			case HEAD_DYING:
				interPhase = true;
				break;
			case HEAD_RISING_1:
			case HEAD_RISING_2:
			case HEAD_ENRAGED_RISING_2:
				interPhase = false;
				interPhaseTimer = -1;
				break;
		}

		headAnimation = currentAnimation;
	}

	private void handAnimations()
	{
		if (hand == null || hand.getRenderable() == null)
		{
			return;
		}

		OlmAnimation currentAnimation = OlmAnimation.fromId(((DynamicObject) hand.getRenderable()).getAnimation().getId());

		if (currentAnimation == handAnimation)
		{
			return;
		}

		switch (currentAnimation)
		{
			case LEFT_HAND_GHOST_CLENCH:
					//only offset cycle if ghost clench happened on attack tick
					//System.out.println("Ghost clench detected, offsetting cycle");
					if (attackCycle == 3 && ticksUntilNextAttack == 4) {
						attackCycle = 1;
						//System.out.println("Ghost clenched on 4, offsetting cycle to 2");
					}
					else if (attackCycle == 1 && ticksUntilNextAttack == 4) {
						attackCycle = 3;
						decrementSpecialCycle();
						//System.out.println("Ghost clenched on 2, offsetting cycle to 4");
						//System.out.println("a special got erroneously incremented, so we decrement the special cycle");
					}
					else {
						//System.out.println("Ghost clenched on an odd number tick, do nothing");
					}
					break;

			case LEFT_HAND_CRYSTALS1:
			case LEFT_HAND_CRYSTALS2:
			case LEFT_HAND_LIGHTNING1:
			case LEFT_HAND_LIGHTNING2:
			case LEFT_HAND_PORTALS1:
			case LEFT_HAND_PORTALS2:
			case LEFT_HAND_HEAL1:
			case LEFT_HAND_HEAL2:
				specialSync(currentAnimation);
				break;
			case LEFT_HAND_CRIPPLING:
				cripple();
//				System.out.println("I am crippling");
				if (attackCycle == 1)
				{
				decrementSpecialCycle();
				savedcripplecounter = 2;
				System.out.println("saved cripple timer for attack 2");
//				System.out.println("Attack cycle is 2, a special was erroneously incremented, so I am decrementing the special attack counter");
				}
				else
				{
//					System.out.println("Attack cycle was not 2, so I am not decrementing the special attack counter");
					System.out.println("Saved cripple timer for attack 4");
					savedcripplecounter = 4;
				}
				break;
			case LEFT_HAND_UNCRIPPLING1:
			case LEFT_HAND_UNCRIPPLING2:
				uncripple();
				System.out.println("I am uncrippling");
				switch (attackCycle)
			{
				case 1: //attack 2 (special)
					if (savedcripplecounter == 2)
					{
						attackCycle = 3;
//						System.out.println("uncripple case 1, debug line 2");
					}
					else
					{
//						System.out.println("uncripple case 1, debug line 6");
					}
					break;
				case 2: //attack 3 (auto)
					if (savedcripplecounter == 2)
					{
						attackCycle = 4;
						System.out.println("uncripple case 2, debug line 3");
					}
					else
					{
//						System.out.println("uncripple case 2, debug line 7");
					}
					break;
				case 3: //attack 4 (null)
					if (savedcripplecounter == 2)
					{
//						System.out.println("uncripple case 3, debug line 4");
					}
					else
					{
						attackCycle = 1;
//						System.out.println("uncripple case 3, debug line 8");
					}
					break;
				case 4: //attack 1 (auto)
					if (savedcripplecounter == 2)
					{
//						System.out.println("uncripple case 4, debug line 1");
					}
					else
					{
						attackCycle = 2;
						System.out.println("uncripple case 4, debug line 5");
					}
					break;
			}
			break;
		}

		handAnimation = currentAnimation;
	}
/*
Debug lines:
Cripple tick 2 (special saved)
1 Uncripple tick 1: next attack will be the spec, so we need to be on tick 1 (we are!)
2 Uncripple tick 2: next attack will be auto then spec, so we need to be on tick 4
3 Uncripple tick 3: next attack will be spec, so we need to be on tick 1
4 Uncripple tick 4: next attack will be auto then spec, so we need to be on tick 4 (we are!)

Cripple tick 4 (null saved)
5 Uncripple tick 1: next attack will be null, so we need to be on tick 3
6 Uncripple tick 2: next attack will be auto then null, so we need to be on tick 2 (we are!)
7 Uncripple tick 3: next attack will be null, so we need to be on tick 3 (we are!)
8 Uncripple tick 4: next attack will be auto then null, so we need to be on tick 2

*/


	public enum PhaseType
	{
		FLAME,
		ACID,
		CRYSTAL,
		UNKNOWN,
	}
}