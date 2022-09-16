package com.xwintop.xJavaFxTool.controller.developTools;

import com.xwintop.xJavaFxTool.services.developTools.JsonConvertToolService;
import com.xwintop.xJavaFxTool.services.developTools.XpathConvertToolService;
import com.xwintop.xJavaFxTool.view.developTools.JsonConvertToolView;
import com.xwintop.xJavaFxTool.view.developTools.XpathConvertToolView;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @ClassName: JsonConvertToolController
 * @Description: Json转换工具
 * @author: xufeng
 * @date: 2018/2/5 17:04
 */

@Getter
@Setter
@Slf4j
@Lazy
@FXMLController
public class XpathConvertToolController extends XpathConvertToolView
{
    private XpathConvertToolService xpathConvertToolService = new XpathConvertToolService(this);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        xpathTextArea.setStyle("-fx-font-size: 30");
    }

    private void initEvent() {
    }

    private void initService() {
    }

    @FXML
    public void createXpathByXmlNodeAction(ActionEvent event) {
        xpathConvertToolService.createXpathByXmlNodeAction();
    }

    @FXML
    public void findXmlNodeByXpathAction(ActionEvent event) {
        xpathConvertToolService.findXmlNodeByXpathAction();
    }
}