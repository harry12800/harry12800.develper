package cn.harry12800.developer;

import java.util.LinkedHashSet;

import cn.harry12800.dbhelper.DBType;
import cn.harry12800.dbhelper.entity.DBField;
import cn.harry12800.dbhelper.entity.DBTable;
import cn.harry12800.tools.EntityMent;

public class Table {

	private String dbName;
	private String javaClazzName;

	private String createSql;
	private String createOracleSql;
	private String tbDesc;
	LinkedHashSet<Field> fields = new LinkedHashSet<Field>();
	LinkedHashSet<Field> keyFields = new LinkedHashSet<Field>();
	LinkedHashSet<Field> allFields = new LinkedHashSet<Field>();

	public Table(DBTable table) {
		this.dbName = table.getName();
		try {
			this.createOracleSql = table.getCreateDDL(DBType.ORACLE);
			this.createOracleSql = this.createOracleSql.replaceAll("\r\n", "\"+\r\n\t\t\"");
			this.createSql = table.getCreateDDL(DBType.MYSQL);
			this.createSql = this.createSql.replaceAll("\r\n", "\"+\r\n\t\t\"");
			System.out.println(createOracleSql);
			System.out.println(createSql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.javaClazzName = EntityMent.columnName2EntityClassName(table.getName());
		this.tbDesc = table.getComment();
		LinkedHashSet<DBField> fields = table.getFields();
		for (DBField dbField : fields) {
			Field field = new Field(dbField);
			if(field.getIsKey()){
				this.keyFields.add(field);
			}
			else{
				this.fields.add(field);
			}
			this.allFields.add(field);
		}
	}

	public LinkedHashSet<Field> getKeyFields() {
		return keyFields;
	}

	public void setKeyFields(LinkedHashSet<Field> keyFields) {
		this.keyFields = keyFields;
	}

	public LinkedHashSet<Field> getAllFields() {
		return allFields;
	}

	public void setAllFields(LinkedHashSet<Field> allFields) {
		this.allFields = allFields;
	}

	/**
	 * 获取createSql
	 *	@return the createSql
	 */
	public String getCreateSql() {
		return createSql;
	}

	/**
	 * 设置createSql
	 * @param createSql the createSql to set
	 */
	public void setCreateSql(String createSql) {
		this.createSql = createSql;
	}

	/**
	 * 获取tbDesc
	 *	@return the tbDesc
	 */
	public String getTbDesc() {
		return tbDesc;
	}

	/**
	 * 获取dbName
	 *	@return the dbName
	 */
	public String getDbName() {
		return dbName;
	}

	/**
	 * 设置dbName
	 * @param dbName the dbName to set
	 */
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	/**
	 * 设置tbDesc
	 * @param tbDesc the tbDesc to set
	 */
	public void setTbDesc(String tbDesc) {
		this.tbDesc = tbDesc;
	}

	/**
	 * 获取javaClazzName
	 *	@return the javaClazzName
	 */
	public String getJavaClazzName() {
		return javaClazzName;
	}

	/**
	 * 设置javaClazzName
	 * @param javaClazzName the javaClazzName to set
	 */
	public void setJavaClazzName(String javaClazzName) {
		this.javaClazzName = javaClazzName;
	}

	/**
	 * 获取fields
	 *	@return the fields
	 */
	public LinkedHashSet<Field> getFields() {
		return fields;
	}

	/**
	 * 设置fields
	 * @param fields the fields to set
	 */
	public void setFields(LinkedHashSet<Field> fields) {
		this.fields = fields;
	}

	/**
	 * 获取createOracleSql
	 *	@return the createOracleSql
	 */
	public String getCreateOracleSql() {
		return createOracleSql;
	}

	/**
	 * 设置createOracleSql
	 * @param createOracleSql the createOracleSql to set
	 */
	public void setCreateOracleSql(String createOracleSql) {
		this.createOracleSql = createOracleSql;
	}

}
