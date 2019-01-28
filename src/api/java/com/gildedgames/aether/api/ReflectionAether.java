package com.gildedgames.aether.api;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionAether
{
	public static final ReflectionEntry EQUIPPED_PROGRESS_MAIN_HAND = new ReflectionEntry("field_187469_f", "equippedProgressMainHand");

	public static final ReflectionEntry ACTIVE_ITEMSTACK_USE_COUNT = new ReflectionEntry("field_184628_bn", "activeItemStackUseCount");

	public static final ReflectionEntry INVULNERABLE_DIMENSION_CHANGE = new ReflectionEntry("field_184851_cj", "invulnerableDimensionChange");

	public static final ReflectionEntry BLOCK_HARDNESS = new ReflectionEntry("field_149782_v", "blockHardness");

	public static final ReflectionEntry IS_JUMPING = new ReflectionEntry("field_70703_bu", "isJumping");

	public static final ReflectionEntry SERVER_CURRENT_TIME = new ReflectionEntry("field_175591_ab", "currentTime");

	public static final ReflectionEntry BUTTON_LIST = new ReflectionEntry("field_146292_n", "buttonList");

	public static final ReflectionEntry ENTITY_RENDER_LAYERS = new ReflectionEntry("field_177097_h", "layerRenderers");

	public static final ReflectionEntry GET_FOV_MODIFIER = new ReflectionEntry("func_78481_a", "getFOVModifier");

	public static final ReflectionEntry ENTITY_RENDERER_LIGHTMAP_COLORS = new ReflectionEntry("lightmapColors", "field_78504_Q");

	public static final ReflectionEntry ENTITY_RENDERER_LIGHTMAP_TEXTURE = new ReflectionEntry("lightmapTexture", "field_78513_d");

	public static final ReflectionEntry ENTITY_RENDERER_TORCH_FLICKER_X = new ReflectionEntry("torchFlickerX", "field_78514_e");

	public static final ReflectionEntry DAMAGE_REDUCE_AMOUNT = new ReflectionEntry("damageReduceAmount", "field_77879_b");

	public static Field getField(final Class clazz, final String... names)
	{
		for (final Field field : clazz.getDeclaredFields())
		{
			for (final String name : names)
			{
				if (field.getName().equals(name))
				{
					field.setAccessible(true);

					return field;
				}
			}
		}

		throw new RuntimeException("Couldn't find field");
	}

	public static Method getMethod(final Class clazz, final Class<?>[] args, final String... names)
	{
		for (final Method method : clazz.getDeclaredMethods())
		{
			for (final String name : names)
			{
				if (method.getName().equals(name))
				{
					final Class<?>[] matching = method.getParameterTypes();

					boolean matches = true;

					if (matching.length != args.length)
					{
						matches = false;
					}
					else
					{
						for (int i = 0; i < args.length; i++)
						{
							if (matching[i] != args[i])
							{
								matches = false;

								break;
							}
						}
					}

					if (matches)
					{
						method.setAccessible(true);

						return method;
					}
				}
			}
		}

		throw new RuntimeException("Couldn't find method");
	}

	public static Object invokeMethod(final Method method, final Object obj, final Object... args)
	{
		try
		{
			return method.invoke(obj, args);
		}
		catch (IllegalAccessException | InvocationTargetException e)
		{
			throw new RuntimeException("Failed to invoke method through reflection", e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getValue(final Field field, final Object obj)
	{
		try
		{
			return (T) field.get(obj);
		}
		catch (final IllegalAccessException e)
		{
			throw new RuntimeException("Failed to fetch field value", e);
		}
	}

	public static void setField(final Field field, final Object instance, final Object value)
	{
		try
		{
			field.set(instance, value);
		}
		catch (final IllegalAccessException e)
		{
			throw new RuntimeException("Failed to fetch field value", e);
		}
	}

	public static class ReflectionEntry
	{
		private final String[] mappings;

		private ReflectionEntry(final String... mappings)
		{
			this.mappings = mappings;
		}

		public String[] getMappings()
		{
			return this.mappings;
		}
	}
}
