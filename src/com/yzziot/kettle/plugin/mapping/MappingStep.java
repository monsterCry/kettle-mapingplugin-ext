package com.yzziot.kettle.plugin.mapping;

import org.pentaho.di.core.BlockingRowSet;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.*;

import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @Description:
 * @Author: timefluid
 * @Date: 2020/3/14
 * @Modified By:
 */
public class MappingStep extends BaseStep implements StepInterface {

    private Database database;
    Map<String,List<MappingField>> mapping = new ConcurrentHashMap<>();
    private Map<String,Integer> fieldIndex = new ConcurrentHashMap<>();
    private RowMetaInterface metaInterface;

    public MappingStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
        super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
    }

    @Override
    public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
        Object[] r = getRow();
        if (r == null) {
            setOutputDone();
            return false;
        } else {
            if (first) {
                first = false;
                for(int i=0;i<getInputRowMeta().getValueMetaList().size();i++) {
                    String typeAndName = this.getInputRowMeta().getValueMetaList().get(i).getName();
                    if(fieldIndex.containsKey(typeAndName)) {
                        fieldIndex.put(typeAndName,i);
                    }
                }
                metaInterface = getInputRowMeta();
            }

            try {
                for(Integer fieldindex:fieldIndex.values()) {
                    String key = new String((r[fieldindex] + ""));
                    List<MappingField> field = mapping.get(key);
                    logDetailed("================================" + key);
                    if(field != null) {

                        //如果存在多次映射，则复制数据
                        for(int i = 0; i < field.size(); i++) {
                            logDetailed("================================" + field.get(i).value);
                            MappingField fs = field.get(i);
                            r[fieldindex] = fs.value;
                            putRow(metaInterface, r);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
        boolean reslt = super.init(smi, sdi);
        MappingStepMeta meta = (MappingStepMeta) smi;
        List<StepMeta> from = getTransMeta().findPreviousSteps(this.getStepMeta());
        for(StepMeta stepMeta :from) {
            logDetailed(stepMeta.getName() + "==============");
            BlockingRowSet rowSet = new BlockingRowSet(this.getTransMeta().getSizeRowset());
            rowSet.setThreadNameFromToCopy(stepMeta.getName(), 0, this.getStepname(), 0);
            StepInterface stepInterface = getTrans().findRunThread(stepMeta.getName());
            stepInterface.getOutputRowSets().add(rowSet);
            stepInterface.identifyErrorOutput();
            this.getInputRowSets().add(rowSet);
        }
        if(meta.getDatabaseDisplayName() == null) {
            return reslt;
        }

        DatabaseMeta databaseMeta = null;
        for(DatabaseMeta meta1 : getTransMeta().getDatabases()) {
            if(meta1.getDisplayName().equals(meta.getDatabaseDisplayName())) {
                databaseMeta = meta1;
                break;
            }
        }
        for(String[] str : meta.getMappingField()) {

            for(String ss:str) {
                logDetailed(str + "-------------映射字段-====================----" + ss);
            }
            fieldIndex.put(str[0],0);
        }
        database = new Database(databaseMeta);
        try {
            database.connect();
            ResultSet set = database.openQuery(meta.getSql());
            logDetailed(meta.getSql());
            while (set.next()) {
                for(String[] str : meta.getMappingField()) {
                    String key = set.getString(str[1]);
                    Object value = set.getObject(str[2]);
                    List<MappingField> fields = mapping.get(key);
                    if(fields == null) {
                        fields = new ArrayList<>();
                        mapping.put(key,fields);
                    }
                    fields.add(new MappingField(0,value));
                }
            }
            database.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            for(StackTraceElement es : e.getStackTrace()) {
                logDetailed("SQL执行异常"  + meta.getSql() + "--" + es.getLineNumber() + es.getClassName());
            }

        }
        return reslt;
    }

    @Override
    public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
        super.dispose(smi, sdi);
    }

    static class MappingField {
        protected int index;
        protected Object value;
        public MappingField(int i,Object v) {
            index = i;
            value = v;
        }
    }

}
