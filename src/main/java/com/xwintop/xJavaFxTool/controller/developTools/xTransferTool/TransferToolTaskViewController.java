package com.xwintop.xJavaFxTool.controller.developTools.xTransferTool;

import com.xwintop.xJavaFxTool.controller.IndexController;
import com.xwintop.xJavaFxTool.services.developTools.xTransferTool.TransferToolTaskViewService;
import com.xwintop.xJavaFxTool.utils.JavaFxViewUtil;
import com.xwintop.xJavaFxTool.view.developTools.xTransferTool.TransferToolTaskViewView;
import com.xwintop.xTransfer.filter.bean.FilterConfig;
import com.xwintop.xTransfer.receiver.bean.ReceiverConfig;
import com.xwintop.xTransfer.sender.bean.SenderConfig;
import com.xwintop.xTransfer.task.entity.TaskConfig;
import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.context.annotation.Lazy;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

@Getter
@Setter
@Slf4j
@Lazy
@FXMLController
public class TransferToolTaskViewController extends TransferToolTaskViewView {
    private TransferToolTaskViewService transferToolTaskViewService = new TransferToolTaskViewService(this);
    private ObservableList<Map<String, String>> propertiesTableData = FXCollections.observableArrayList();
    private ObservableList<String> receiverConfigListData = FXCollections.observableArrayList();
    private ObservableList<String> filterConfigsListData = FXCollections.observableArrayList();
    private ObservableList<String> senderConfigListData = FXCollections.observableArrayList();
    private String[] triggerTypeChoiceBoxStrings = new String[]{"SIMPLE", "CRON"};

    private TaskConfig taskConfig;

    private String fileName;
    private String tabName;

    public static FXMLLoader getFXMLLoader() {
        FXMLLoader fXMLLoader = new FXMLLoader(IndexController.class.getResource("/com/xwintop/xJavaFxTool/fxmlView/developTools/xTransferTool/TransferToolTaskView.fxml"));
        return fXMLLoader;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        JavaFxViewUtil.setTableColumnMapValueFactory(propertiesKeyTableColumn, "key");
        JavaFxViewUtil.setTableColumnMapValueFactory(propertiesValueTableColumn, "value");
        propertiesTableView.setItems(propertiesTableData);
        triggerTypeChoiceBox.getItems().addAll(triggerTypeChoiceBoxStrings);
        triggerTypeChoiceBox.setValue(triggerTypeChoiceBox.getItems().get(0));
        JavaFxViewUtil.setSpinnerValueFactory(intervalTimeSpinner, 0, Integer.MAX_VALUE, 5);
        JavaFxViewUtil.setSpinnerValueFactory(executeTimesSpinner, -1, Integer.MAX_VALUE);
        receiverConfigListView.setItems(receiverConfigListData);
        filterConfigsListView.setItems(filterConfigsListData);
        senderConfigListView.setItems(senderConfigListData);
    }

    private void initEvent() {
        triggerTypeChoiceBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (triggerTypeChoiceBoxStrings[0].equals(newValue)) {
                    triggerCronTextField.setDisable(true);
                    intervalTimeSpinner.setDisable(false);
                    executeTimesSpinner.setDisable(false);
                } else if (triggerTypeChoiceBoxStrings[1].equals(newValue)) {
                    triggerCronTextField.setDisable(false);
                    intervalTimeSpinner.setDisable(true);
                    executeTimesSpinner.setDisable(true);
                }
            }
        });
        serviceViewTabPane.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                MenuItem menu_RemoveAll = new MenuItem("关闭所有");
                menu_RemoveAll.setOnAction(event1 -> {
                    serviceViewTabPane.getTabs().clear();
                    transferToolTaskViewService.getServiceViewTabMap().clear();
                });
                serviceViewTabPane.setContextMenu(new ContextMenu(menu_RemoveAll));
            }
        });
        receiverConfigListView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                Menu menu = new Menu("添加");
                String packageName = "com.xwintop.xTransfer.receiver.bean";
                String[] classNameS = new String[]{
                        "ReceiverConfigEmail",
                        "ReceiverConfigFs",
                        "ReceiverConfigFsSplit",
                        "ReceiverConfigFtp",
                        "ReceiverConfigIbmMq",
                        "ReceiverConfigJms",
                        "ReceiverConfigKafka",
                        "ReceiverConfigRabbitMq",
                        "ReceiverConfigRocketMq",
                        "ReceiverConfigActiveMq",
                        "ReceiverConfigSftp"};
                for (String className : classNameS) {
                    MenuItem menuAdd = new MenuItem(className);
                    menuAdd.setOnAction(event1 -> {
                        try {
                            Object configObject = Class.forName(packageName + "." + className).newInstance();
                            taskConfig.getReceiverConfig().add((ReceiverConfig) configObject);
                            receiverConfigListData.add(((ReceiverConfig) configObject).getServiceName());
                            int selectIndex = receiverConfigListData.size() - 1;
                            receiverConfigListView.getSelectionModel().select(selectIndex);
                            transferToolTaskViewService.addServiceViewTabPane(configObject, selectIndex);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    menu.getItems().add(menuAdd);
                }
//                ClassLoader loader = Thread.currentThread().getContextClassLoader();
//                String packagePath = packageName.replace(".", "/");
//                URL url = loader.getResource(packagePath);
                MenuItem menu_Copy = new MenuItem("复制选中行");
                menu_Copy.setOnAction(event1 -> {
                    String selectString = receiverConfigListView.getSelectionModel().getSelectedItem();
                    receiverConfigListData.add(selectString);
                    try {
                        taskConfig.getReceiverConfig().add((ReceiverConfig) BeanUtils.cloneBean(taskConfig.getReceiverConfig().get(receiverConfigListView.getSelectionModel().getSelectedIndex())));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                MenuItem menu_Remove = new MenuItem("删除选中行");
                menu_Remove.setOnAction(event1 -> {
                    taskConfig.getReceiverConfig().remove(receiverConfigListView.getSelectionModel().getSelectedIndex());
//                    receiverConfigListData.remove(receiverConfigListView.getSelectionModel().getSelectedItem());
                    receiverConfigListData.remove(receiverConfigListView.getSelectionModel().getSelectedIndex());
                });
                MenuItem menu_RemoveAll = new MenuItem("删除所有");
                menu_RemoveAll.setOnAction(event1 -> {
                    receiverConfigListData.clear();
                    taskConfig.getReceiverConfig().clear();
                });
                receiverConfigListView.setContextMenu(new ContextMenu(menu, menu_Copy, menu_Remove, menu_RemoveAll));
            } else if (event.getButton() == MouseButton.PRIMARY) {
                int selectIndex = receiverConfigListView.getSelectionModel().getSelectedIndex();
                if (receiverConfigListView.getSelectionModel().getSelectedItems() == null || selectIndex == -1) {
                    return;
                }
                transferToolTaskViewService.addServiceViewTabPane(taskConfig.getReceiverConfig().get(selectIndex), selectIndex);
            }
        });
        filterConfigsListView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                Menu menu = new Menu("添加");
                String packageName = "com.xwintop.xTransfer.filter.bean";
                String[] classNameS = new String[]{
                        "FilterConfigBackup",
                        "FilterConfigCompress",
                        "FilterConfigDecompress",
                        "FilterConfigEncryptDecrypt",
                        "FilterConfigOracleSqlldr",
                        "FilterConfigGroovyScript",
                        "FilterConfigPythonScript",
                        "FilterConfigJavaScript",
                        "FilterConfigLuaScript",
                };
                for (String className : classNameS) {
                    MenuItem menuAdd = new MenuItem(className);
                    menuAdd.setOnAction(event1 -> {
                        try {
                            Object configObject = Class.forName(packageName + "." + className).newInstance();
                            taskConfig.getFilterConfigs().add((FilterConfig) configObject);
                            filterConfigsListData.add(((FilterConfig) configObject).getServiceName());
                            int selectIndex = filterConfigsListData.size() - 1;
                            filterConfigsListView.getSelectionModel().select(selectIndex);
                            transferToolTaskViewService.addServiceViewTabPane(configObject, selectIndex);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    menu.getItems().add(menuAdd);
                }
//                ClassLoader loader = Thread.currentThread().getContextClassLoader();
//                String packagePath = packageName.replace(".", "/");
//                URL url = loader.getResource(packagePath);
                MenuItem menu_Copy = new MenuItem("复制选中行");
                menu_Copy.setOnAction(event1 -> {
                    String selectString = filterConfigsListView.getSelectionModel().getSelectedItem();
                    filterConfigsListData.add(selectString);
                    try {
                        taskConfig.getFilterConfigs().add((FilterConfig) BeanUtils.cloneBean(taskConfig.getFilterConfigs().get(filterConfigsListView.getSelectionModel().getSelectedIndex())));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                MenuItem menu_Remove = new MenuItem("删除选中行");
                menu_Remove.setOnAction(event1 -> {
                    taskConfig.getFilterConfigs().remove(filterConfigsListView.getSelectionModel().getSelectedIndex());
                    filterConfigsListData.remove(filterConfigsListView.getSelectionModel().getSelectedIndex());
                });
                MenuItem menu_RemoveAll = new MenuItem("删除所有");
                menu_RemoveAll.setOnAction(event1 -> {
                    filterConfigsListData.clear();
                    taskConfig.getFilterConfigs().clear();
                });
                filterConfigsListView.setContextMenu(new ContextMenu(menu, menu_Copy, menu_Remove, menu_RemoveAll));
            } else if (event.getButton() == MouseButton.PRIMARY) {
                int selectIndex = filterConfigsListView.getSelectionModel().getSelectedIndex();
                if (filterConfigsListView.getSelectionModel().getSelectedItems() == null || selectIndex == -1) {
                    return;
                }
                transferToolTaskViewService.addServiceViewTabPane(taskConfig.getFilterConfigs().get(selectIndex), selectIndex);
            }
        });
        senderConfigListView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                Menu menu = new Menu("添加");
                String packageName = "com.xwintop.xTransfer.sender.bean";
                String[] classNameS = new String[]{
                        "SenderConfigEmail",
                        "SenderConfigFs",
                        "SenderConfigFtp",
                        "SenderConfigIbmMq",
                        "SenderConfigJms",
                        "SenderConfigKafka",
                        "SenderConfigRabbitMq",
                        "SenderConfigRocketMq",
                        "SenderConfigActiveMq",
                        "SenderConfigSftp"
                };
                for (String className : classNameS) {
                    MenuItem menuAdd = new MenuItem(className);
                    menuAdd.setOnAction(event1 -> {
                        try {
                            Object configObject = Class.forName(packageName + "." + className).newInstance();
                            taskConfig.getSenderConfig().add((SenderConfig) configObject);
                            senderConfigListData.add(((SenderConfig) configObject).getServiceName());
                            int selectIndex = senderConfigListData.size() - 1;
                            senderConfigListView.getSelectionModel().select(selectIndex);
                            transferToolTaskViewService.addServiceViewTabPane(configObject, selectIndex);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    menu.getItems().add(menuAdd);
                }
//                ClassLoader loader = Thread.currentThread().getContextClassLoader();
//                String packagePath = packageName.replace(".", "/");
//                URL url = loader.getResource(packagePath);
                MenuItem menu_Copy = new MenuItem("复制选中行");
                menu_Copy.setOnAction(event1 -> {
                    String selectString = senderConfigListView.getSelectionModel().getSelectedItem();
                    senderConfigListData.add(selectString);
                    try {
                        taskConfig.getSenderConfig().add((SenderConfig) BeanUtils.cloneBean(taskConfig.getSenderConfig().get(senderConfigListView.getSelectionModel().getSelectedIndex())));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                MenuItem menu_Remove = new MenuItem("删除选中行");
                menu_Remove.setOnAction(event1 -> {
                    taskConfig.getSenderConfig().remove(senderConfigListView.getSelectionModel().getSelectedIndex());
//                    senderConfigListData.remove(senderConfigListView.getSelectionModel().getSelectedItem());
                    senderConfigListData.remove(senderConfigListView.getSelectionModel().getSelectedIndex());
                });
                MenuItem menu_RemoveAll = new MenuItem("删除所有");
                menu_RemoveAll.setOnAction(event1 -> {
                    senderConfigListData.clear();
                    taskConfig.getSenderConfig().clear();
                });
                senderConfigListView.setContextMenu(new ContextMenu(menu, menu_Copy, menu_Remove, menu_RemoveAll));
            } else if (event.getButton() == MouseButton.PRIMARY) {
                int selectIndex = senderConfigListView.getSelectionModel().getSelectedIndex();
                if (senderConfigListView.getSelectionModel().getSelectedItems() == null || selectIndex == -1) {
                    return;
                }
                transferToolTaskViewService.addServiceViewTabPane(taskConfig.getSenderConfig().get(selectIndex), selectIndex);
            }
        });

        JavaFxViewUtil.addTableViewOnMouseRightClickMenu(propertiesTableView);
    }

    private void initService() {
    }

    @FXML
    void saveTaskConfigAction(ActionEvent event) {
        try {
            transferToolTaskViewService.saveTaskConfigAction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setData(TransferToolController xTransferToolController, TaskConfig taskConfig) {
        transferToolTaskViewService.setTransferToolController(xTransferToolController);
        this.taskConfig = taskConfig;
        nameTextField.setText(this.taskConfig.getName());
        isEnableCheckBox.setSelected(this.taskConfig.getIsEnable());
        taskTypeTextField.setText(this.taskConfig.getTaskType());
        triggerTypeChoiceBox.setValue(this.taskConfig.getTriggerType());
        intervalTimeSpinner.getValueFactory().setValue(this.taskConfig.getIntervalTime());
        executeTimesSpinner.getValueFactory().setValue(this.taskConfig.getExecuteTimes());
        triggerCronTextField.setText(this.taskConfig.getTriggerCron());
        isStatefullJobCheckBox.setSelected(this.taskConfig.getIsStatefulJob());
        receiverConfigListData.clear();
        for (ReceiverConfig receiverConfig : this.taskConfig.getReceiverConfig()) {
            receiverConfigListData.add(receiverConfig.getServiceName());
        }
        filterConfigsListData.clear();
        for (FilterConfig filterConfig : this.taskConfig.getFilterConfigs()) {
            filterConfigsListData.add(filterConfig.getServiceName());
        }
        senderConfigListData.clear();
        for (SenderConfig senderConfig : this.taskConfig.getSenderConfig()) {
            senderConfigListData.add(senderConfig.getServiceName());
        }
        propertiesTableData.clear();
        this.taskConfig.getProperties().forEach((key, value) -> {
            Map<String, String> map = new HashMap<>();
            map.put("key", key);
            map.put("value", value.toString());
            propertiesTableData.add(map);
        });
    }
}