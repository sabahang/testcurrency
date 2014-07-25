databaseChangeLog = {

	changeSet(author: "hesamyou (generated)", id: "1406237120272-1") {
		createTable(tableName: "CURRENCY") {
			column(autoIncrement: "true", name: "ID", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "CONSTRAINT_5")
			}

			column(name: "VERSION", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "NAME", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "RATE_ONE", type: "float") {
				constraints(nullable: "false")
			}

			column(name: "RATE_THREE", type: "float") {
				constraints(nullable: "false")
			}

			column(name: "RATE_TWO", type: "float") {
				constraints(nullable: "false")
			}

			column(name: "SYMBOL", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "TIME_ONE", type: "TIMESTAMP") {
				constraints(nullable: "false")
			}

			column(name: "TIME_THREE", type: "TIMESTAMP") {
				constraints(nullable: "false")
			}

			column(name: "TIME_TWO", type: "TIMESTAMP") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "hesamyou (generated)", id: "1406237120272-2") {
		createTable(tableName: "EXCHANGE_RATE") {
			column(autoIncrement: "true", name: "ID", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "CONSTRAINT_D")
			}

			column(name: "VERSION", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "BASECURRENCY_ID", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "DATE_ONE", type: "TIMESTAMP") {
				constraints(nullable: "false")
			}

			column(name: "DATE_THREE", type: "TIMESTAMP") {
				constraints(nullable: "false")
			}

			column(name: "DATE_TWO", type: "TIMESTAMP") {
				constraints(nullable: "false")
			}

			column(name: "EXCHANGERATE_ONE", type: "float") {
				constraints(nullable: "false")
			}

			column(name: "EXCHANGERATE_THREE", type: "float") {
				constraints(nullable: "false")
			}

			column(name: "EXCHANGERATE_TWO", type: "float") {
				constraints(nullable: "false")
			}

			column(name: "TARGETCURRENCY_ID", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "hesamyou (generated)", id: "1406237120272-3") {
		addForeignKeyConstraint(baseColumnNames: "BASECURRENCY_ID", baseTableName: "EXCHANGE_RATE", baseTableSchemaName: "PUBLIC", constraintName: "FK_HXF4QWCATL3TVIUP5K1KYVG5R", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", onUpdate: "RESTRICT", referencedColumnNames: "ID", referencedTableName: "CURRENCY", referencedTableSchemaName: "PUBLIC", referencesUniqueColumn: "false")
	}

	changeSet(author: "hesamyou (generated)", id: "1406237120272-4") {
		addForeignKeyConstraint(baseColumnNames: "TARGETCURRENCY_ID", baseTableName: "EXCHANGE_RATE", baseTableSchemaName: "PUBLIC", constraintName: "FK_24LYBJ63DMSDLTGWGFR6PXFT7", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", onUpdate: "RESTRICT", referencedColumnNames: "ID", referencedTableName: "CURRENCY", referencedTableSchemaName: "PUBLIC", referencesUniqueColumn: "false")
	}
}
