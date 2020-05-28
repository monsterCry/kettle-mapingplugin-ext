package com.yzziot.kettle.plugin.mapping;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.*;
import org.pentaho.di.trans.steps.mapping.MappingData;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * @Description:
 * @Author: timefluid
 * @Date: 2020/3/14
 * @Modified By:
 */

@Step(
        id="MappingPlugin",
        image="icon.png",
        description="Template Plugin", name = "MappingPlugin")
public class MappingStepMeta extends BaseStepMeta implements StepMetaInterface {

    private String sql;
    private String url;
    private String username;
    private String password;
    private String databasename;
    private String databaseDisplayName;
    private String port;
    private String dbType;
    private List<String[]> mappingField = new ArrayList<>(2);

    public MappingStepMeta() {
        super();
    }

    @Override
    public void setDefault() {

    }

    @Override
    public void loadXML(Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore) throws KettleXMLException {
        super.loadXML(stepnode, databases, metaStore);
        this.sql = XMLHandler.getTagValue(stepnode, "Sql").trim();
        this.username = XMLHandler.getTagValue(stepnode, "Database","username");
        this.url =  XMLHandler.getTagValue(stepnode, "Database","url");
        this.password = XMLHandler.getTagValue(stepnode, "Database","password");
        this.databasename = XMLHandler.getTagValue(stepnode, "Database","name");
        this.port = XMLHandler.getTagValue(stepnode, "Database","port");
        this.dbType = XMLHandler.getTagValue(stepnode, "Database","DbType");
        this.databaseDisplayName = XMLHandler.getTagValue(stepnode, "Database","displayname");
        String[] strs =  XMLHandler.getTagValue(stepnode, "Field").split("\\|");
        mappingField.clear();
        for(String str:strs) {
            String[] splis = str.split(",");
            for(int i = 0; i < splis.length; i++) {
                splis[i] = splis[i].trim();
            }
            mappingField.add(splis);
        }
    }

    @Override
    public String getXML() throws KettleException {
        String retval = super.getXML();
        retval = retval + "    <" + "Sql" + ">" + Const.CR;
        retval = retval + sql + Const.CR;
        retval = retval + "    </" + "Sql" + ">" + Const.CR;

        retval = retval + "    <" + "Database" + ">" + Const.CR;
        retval = retval + "    <" + "name" + ">" + databasename + "</name>" + Const.CR;
        retval = retval + "    <" + "displayname" + ">" + databaseDisplayName + "</displayname>" + Const.CR;
        retval = retval + "    <" + "username" + ">" + username + "</username>" + Const.CR;
        retval = retval + "    <" + "DbType" + ">" + dbType + "</DbType>" + Const.CR;
        retval = retval + "    <" + "url" + ">" + url + "</url>" + Const.CR;
        retval = retval + "    <" + "port" + ">" + port + "</port>" + Const.CR;
        retval = retval + "    <" + "password" + ">" + password + "</password>" + Const.CR;
        retval = retval + "    </" + "Database" + ">" + Const.CR;

        StringJoiner stringJoiner = new StringJoiner("|");
        for(String[] str:mappingField) {
            StringJoiner stringJoiner1 = new StringJoiner(",");
            for(String string:str) {
                stringJoiner1.add(string);
            }
            stringJoiner.add(stringJoiner1.toString());
        }
        retval = retval + "    <" + "Field" + ">" + Const.CR;
        retval = retval + stringJoiner.toString() + Const.CR;
        retval = retval + "    </" + "Field" + ">" + Const.CR;
        logDetailed(retval);
        return retval;
    }

    @Override
    public void saveRep(Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step) throws KettleException {
        super.saveRep(rep, metaStore, id_transformation, id_step);
        logDetailed("--------------------------保存转化信息----------------");
    }

    @Override
    public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int i, TransMeta transMeta, Trans trans) {
        return new MappingStep(stepMeta,stepDataInterface,i,transMeta,trans);
    }

    @Override
    public StepDataInterface getStepData() {
        return new MappingData();
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String[]> getMappingField() {
        return mappingField;
    }

    public void setMappingField(List<String[]> mappingField) {
        this.mappingField = mappingField;
    }

    public void setDatabasename(String databasename) {
        this.databasename = databasename;
    }

    public String getDatabasename() {
        return databasename;
    }

    public String getPort() {
        return port;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDatabaseDisplayName() {
        return databaseDisplayName;
    }

    public void setDatabaseDisplayName(String databaseDisplayName) {
        this.databaseDisplayName = databaseDisplayName;
    }
}
