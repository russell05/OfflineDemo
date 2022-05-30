package com.example.bdtrack.View;

import com.example.bdtrack.Util.ResUtil;
import ohos.agp.components.*;
import ohos.agp.components.element.Element;
import ohos.agp.components.element.ShapeElement;
import ohos.app.Context;
import com.example.bdtrack.ResourceTable;


public class RightItemView extends DependentLayout {

    private Text text;

    private Button button;

    public RightItemView(Context context) {
        this(context, null, null);
    }

    public RightItemView(Context context, AttrSet attrSet) {
        this(context, attrSet, null);
    }

    public RightItemView(Context context, AttrSet attrSet, String styleName) {
        super(context, attrSet, styleName);
        init(context, attrSet);
    }

    private void init(Context context, AttrSet attrSet) {
        LayoutScatter scatter = LayoutScatter.getInstance(context);
        Component component = scatter.parse(ResourceTable.Layout_right_item_view, null, false);
        super.addComponent(component);


        setBackground(new ShapeElement(context, ResourceTable.Graphic_right_Item_bg));
        text = (Text) findComponentById(ResourceTable.Id_item_text);
        button = findComponentById(ResourceTable.Id_button_right_item);
    }

    public void setText(String content) {
        text.setText(content);
    }

    public void setBgImage(int resourceId) {
        Element defaultButton =  ResUtil.getDrawable(this.getContext(), resourceId);
        button.setAroundElements(defaultButton, null, null, null);
    }

    public void setButtonTitle(String content) {
        button.setText(content);
    }
}
