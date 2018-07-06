package org.hydrofoil.core;

import org.hydrofoil.core.management.DataSourceManager;
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
    private DataSourceManager dataSourceManager;

    /**
     * schema manager
     */
    private SchemaManager schemaManager;

    StandardGraphContext(DataSourceManager dataSourceManager,
                         SchemaManager schemaManager) {
        super();
        this.dataSourceManager = dataSourceManager;
        this.schemaManager = schemaManager;
    }

    /**
     * @return DataManager
     * @see StandardGraphContext#dataSourceManager
     **/
    @Override
    protected DataSourceManager getDataManager() {
        return dataSourceManager;
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
