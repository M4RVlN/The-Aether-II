package com.gildedgames.aether.client.models.entities.attachments;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

/**
 * Swet - Undefined
 * Created using Tabula 5.1.0
 */
public class ModelSwetJelly extends ModelBase
{
    public ModelRenderer Base;
    public ModelRenderer Front;
    public ModelRenderer Top;
    public ModelRenderer Right;
    public ModelRenderer Left;
    public ModelRenderer Back;
    public ModelRenderer Bottom;

    public ModelSwetJelly()
    {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.Right = new ModelRenderer(this, 0, 47);
        this.Right.setRotationPoint(-10.0F, 0.0F, 0.0F);
        this.Right.addBox(-6.5F, -6.5F, 0.0F, 13, 13, 2, 0.0F);
        this.setRotateAngle(Right, 0.0F, 1.5707963267948966F, 0.0F);
        this.Back = new ModelRenderer(this, 0, 47);
        this.Back.setRotationPoint(0.0F, 0.0F, 10.0F);
        this.Back.addBox(-6.5F, -6.5F, 0.0F, 13, 13, 2, 0.0F);
        this.setRotateAngle(Back, 0.0F, 3.141592653589793F, 0.0F);
        this.Base = new ModelRenderer(this, 0, 0);
        this.Base.setRotationPoint(0.0F, 14.0F, 0.0F);
        this.Base.addBox(-8.0F, -8.0F, -8.0F, 16, 16, 16, 0.0F);
        this.Front = new ModelRenderer(this, 0, 47);
        this.Front.setRotationPoint(0.0F, 0.0F, -10.0F);
        this.Front.addBox(-6.5F, -6.5F, 0.0F, 13, 13, 2, 0.0F);
        this.Top = new ModelRenderer(this, 0, 47);
        this.Top.mirror = true;
        this.Top.setRotationPoint(0.0F, -10.0F, 0.0F);
        this.Top.addBox(-6.5F, -6.5F, 0.0F, 13, 13, 2, 0.0F);
        this.setRotateAngle(Top, -1.5707963267948966F, 0.0F, 0.0F);
        this.Bottom = new ModelRenderer(this, 0, 32);
        this.Bottom.setRotationPoint(0.0F, 8.0F, 0.0F);
        this.Bottom.addBox(-6.5F, 0.0F, -6.5F, 13, 2, 13, 0.0F);
        this.Left = new ModelRenderer(this, 0, 47);
        this.Left.mirror = true;
        this.Left.setRotationPoint(10.0F, 0.0F, 0.0F);
        this.Left.addBox(-6.5F, -6.5F, 0.0F, 13, 13, 2, 0.0F);
        this.setRotateAngle(Left, 0.0F, -1.5707963267948966F, 0.0F);
        this.Base.addChild(this.Right);
        this.Base.addChild(this.Back);
        this.Base.addChild(this.Front);
        this.Base.addChild(this.Top);
        this.Base.addChild(this.Bottom);
        this.Base.addChild(this.Left);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.7F);
        this.Base.render(f5);
        GlStateManager.disableBlend();
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}