package com.yzziot.kettle.plugin.mapping;

import org.eclipse.swt.SWT;

import org.eclipse.swt.events.*;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;


import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import java.util.*;





/**
 * @Description:
 * @Author: timefluid
 * @Date: 2020/3/14
 * @Modified By:
 */
public class MappingStepDialog extends BaseStepDialog implements StepDialogInterface,SelectionListener {


    private Text stepNameText;
    private Text sqlText;
    private TableView table;
    private Combo databaseList;

    public MappingStepDialog(Shell parent, BaseStepMeta baseStepMeta, TransMeta transMeta, String stepname) {
        super(parent, baseStepMeta, transMeta, stepname);
    }

    public MappingStepDialog(Shell parent, Object baseStepMeta, TransMeta transMeta, String stepname) {
        super(parent, (BaseStepMeta)baseStepMeta, transMeta, stepname);
    }

    @Override
    public String open() {

        Display display = getParent().getDisplay();
        shell = new Shell(display,SWT.CLOSE|SWT.RESIZE);
        shell.setText("映射扩展");
        shell.setMinimumSize(600,500);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        shell.setLayout(gridLayout);
        gridLayout.horizontalSpacing = 10;


        //步骤名称
        Label stepNameLabel = new Label(shell,SWT.NONE);
        stepNameLabel.setText("请输入步骤名称");
        stepNameText = new Text(shell,SWT.SINGLE|SWT.BORDER);
        GridData stepGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        stepGridData.horizontalSpan = 2;
        stepNameText.setLayoutData(stepGridData);

        //
        Label databaseLabel = new Label(shell,SWT.NONE);
        databaseLabel.setText("选择数据库");
        databaseList = new Combo(shell,SWT.NONE);
        databaseList.select(0);
        databaseList.setLayoutData(stepGridData);

        //sql
        Label sqlLabel = new Label(shell,SWT.NONE);
        sqlLabel.setText("SQL");
        sqlText = new Text(shell,SWT.BORDER|SWT.MULTI);
        GridData fillGrid = new GridData(GridData.FILL_BOTH);
        fillGrid.horizontalSpan = 2;
        sqlText.setLayoutData(fillGrid);

        //字段
        Label mapLabel = new Label(shell,SWT.NONE);
        mapLabel.setText("映射");
        table = new TableView(transMeta,shell,67586,new ColumnInfo[]{
                new ColumnInfo("源字段",SWT.ALL),
                new ColumnInfo("目标字段",SWT.ALL),
                new ColumnInfo("目标值",SWT.ALL),
        },0,null,props);
        table.setLayoutData(fillGrid);

        //确认取消
        GridData center = new GridData(GridData.CENTER);
        Button ok = new Button(shell,SWT.PUSH);
        ok.setText("确认");
        ok.addSelectionListener(this);
        ok.setLayoutData(center);
        ok.setData("OK");

        Button cancle = new Button(shell,SWT.PUSH);
        cancle.setText("取消");
        cancle.addSelectionListener(this);
        cancle.setLayoutData(center);
        cancle.setData("Cancle");

        init();
        shell.pack();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()){ display.sleep();}
        }

        return stepname;
    }

    private void init() {
        MappingStepMeta mappingStepMeta = (MappingStepMeta) stepMeta.getStepMetaInterface();
        for(int i = 0; i < transMeta.getDatabases().size(); i++) {
            String name = transMeta.getDatabases().get(i).getDisplayName();
            databaseList.add(name);
            String dbName = mappingStepMeta.getDatabaseDisplayName();
            if(dbName != null && dbName.equals(name)) {
                databaseList.select(i);
            }
        }
        ArrayList<String[]> field = (ArrayList<String[]>) mappingStepMeta.getMappingField();
        for(String[] strs : field) {
            table.add(strs[0],strs[1],strs[2]);
        }
        table.remove(0);
        if(mappingStepMeta.getSql() != null) {
            sqlText.setText(mappingStepMeta.getSql());
        }
        if(!"".equals(stepname) && null != stepname) {
            stepNameText.setText(stepname);
        }
    }


    @Override
    public void widgetSelected(SelectionEvent selectionEvent) {
        Button data = (Button) selectionEvent.getSource();
        if(!"".equals(stepNameText.getText()) && null != stepNameText.getText()) {
            stepname = stepNameText.getText();
        }
        if(!"OK".equals(data.getData())) {
            shell.dispose();
            return;
        }
        String database = databaseList.getItem(databaseList.getSelectionIndex());
        String sql = sqlText.getText().trim();
        ArrayList<String[]> field = new ArrayList<>();
        for(int i = 0; i < table.getItemCount(); i++) {
            String[] values = table.getItem(i);
            boolean canUse = true;
            for(String str : values) {
                logDetailed(str);
                if("".equals(str) || null == str) {
                    canUse = false;
                }
            }
            if(canUse) {
                field.add(values);
            }
        }
        logDetailed(sql + database);
        MappingStepMeta mappingStepMeta = (MappingStepMeta) stepMeta.getStepMetaInterface();
        mappingStepMeta.setDatabaseDisplayName(database);
        mappingStepMeta.setSql(sql);
        mappingStepMeta.setMappingField(field);
        shell.dispose();
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent selectionEvent) {

    }
}
