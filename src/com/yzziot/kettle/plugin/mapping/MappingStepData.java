package com.yzziot.kettle.plugin.mapping;

import org.pentaho.di.core.database.Database;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

/**
 * @Description:
 * @Author: timefluid
 * @Date: 2020/3/14
 * @Modified By:
 */
public class MappingStepData extends BaseStepData implements StepDataInterface {
    private Database database;

    public MappingStepData() {
        super();

    }
}
