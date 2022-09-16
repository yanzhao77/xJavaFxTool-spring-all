package com.xwintop.xJavaFxTool.view.developTools;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName: JsonConvertToolView
 * @Description: Json转换工具
 * @author: xufeng
 * @date: 2018/2/5 17:04
 */

@Getter
@Setter
public abstract class XpathConvertToolView implements Initializable
{
    @FXML
    public TextField xpathTextArea;
    @FXML
    protected TextArea allXmlTextArea;
    @FXML
    protected TextArea nodeXmlTextArea;

}