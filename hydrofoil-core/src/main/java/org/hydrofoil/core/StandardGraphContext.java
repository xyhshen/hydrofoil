package org.hydrofoil.core;

import org.hydrofoil.core.management.DataManager;
import org.hydrofoil.core.management.SchemaManager;
import org.hydrofoil.core.standard.internal.AbstractGraphQueryRunner;

/**
 * GraphContext
 * <p>
 * package org.hydrofoil.core
 *
 * @author xie_yh
 * @date 2018/7/2 16:26
 */
public final class StandardGraphContext extends AbstractGraphQueryRunner {

    /**
     * data manager
     */
    private DataManager dataManager;

    /**
     * schema manager
     */
    private SchemaManager schemaManager;

    StandardGraphContext(DataManager dataManager,
                         SchemaManager schemaManager) {
        super();
        this.dataManager = dataManager;
        this.schemaManager = schemaManager;
    }

    /**
     * @return DataManager
     * @see StandardGraphContext#dataManager
     **/
    @Override
    protected DataManager getDataManager() {
        return dataManager;
    }

    /**
     * @return SchemaManager
     * @see StandardGraphContext#schemaManager
     **/
    @Override
    protected SchemaManager getSchemaManager() {
        return schemaManager;
    }
}
