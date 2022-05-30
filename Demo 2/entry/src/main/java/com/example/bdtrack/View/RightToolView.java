package com.example.bdtrack.View;

import com.example.bdtrack.ResourceTable;
import com.example.bdtrack.Util.ResUtil;
import ohos.agp.components.*;
import ohos.agp.components.element.Element;
import ohos.agp.components.element.ShapeElement;
import ohos.app.Context;

public class RightToolView extends DependentLayout {

    private Text text;

    private Button button;

    public RightToolView(Context context) {
        this(context, null, null);
    }

    public RightToolView(Context context, AttrSet attrSet) {
        this(context, attrSet, null);
    }

    public RightToolView(Context context, AttrSet attrSet, String styleName) {
        super(context, attrSet, styleName);
        init(context, attrSet);
    }

    private void init(Context context, AttrSet attrSet) {
        LayoutScatter scatter = LayoutScatter.getInstance(context);
        Component component = scatter.parse(ResourceTable.Layout_right_tool_view, null, false);
        super.addComponent(component);


        setBackground(new ShapeElement(context, ResourceTable.Graphic_button));
        text = (Text) findComponentById(ResourceTable.Id_text_tool);
        button = findComponentById(ResourceTable.Id_button_tool);
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
