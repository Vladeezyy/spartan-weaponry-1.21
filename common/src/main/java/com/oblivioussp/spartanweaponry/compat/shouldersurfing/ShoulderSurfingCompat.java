package com.oblivioussp.spartanweaponry.compat.shouldersurfing;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;

public class ShoulderSurfingCompat
{
	public static boolean isShoulderSurfing()
	{
		return IShoulderSurfing.getInstance().isShoulderSurfing();
	}
}
