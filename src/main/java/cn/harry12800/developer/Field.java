package cn.harry12800.developer;

import cn.harry12800.dbhelper.entity.DBField;
import cn.harry12800.tools.EntityMent;

public class Field {

	
	private String comments="";
	private String content="";
	private String dbName="";
	private String javaName="";
	private String javaFieldName="";
	private String dbType="";
	private String javaType="";
	private String tableFieldAnno;

	public Field(DBField field) {
		this.comments = field.getComment()==null?"": field.getComment();
		this.dbName = field.getName();
		this.javaName = EntityMent.columnName2EntityAttrName(field.getName()).replaceAll(" ", "");
		this.dbType =  field.getType();
		this.javaType = EntityMent.getDb2attrMap(field.getType());
		if (javaType == null)
			javaType = "" + field.getType();
		
		char[] cs=javaName.toCharArray();
        cs[0]-=32;
        javaFieldName= String.valueOf(cs);
		if(field.isPrimaryKey)
			this.tableFieldAnno=("@DbField(value=\"主键\",isKey=true,type=0, title = \"主键\",show=false, canAdd = false, canEdit = false, dbFieldName = \""+field.getName()+"\")");
		else{
			this.tableFieldAnno=("@DbField(value=\""+field.getComment()+"\",type=1,sort=1, title =\""+field.getComment()+"\", exp=true,  canAdd = true, canEdit = false, canSearch = false, dbFieldName = \""+field.getName()+"\")");
		}
	}

	/**
	 * 获取javaFieldName
	 *	@return the javaFieldName
	 */
	public String getJavaFieldName() {
		return javaFieldName;
	}

	/**
	 * 设置javaFieldName
	 * @param javaFieldName the javaFieldName to set
	 */
	public void setJavaFieldName(String javaFieldName) {
		this.javaFieldName = javaFieldName;
	}

	/**
	 * 获取comments
	 *	@return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * 设置comments
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * 获取content
	 *	@return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * 设置content
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
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
	 * 获取javaName
	 *	@return the javaName
	 */
	public String getJavaName() {
		return javaName;
	}

	/**
	 * 设置javaName
	 * @param javaName the javaName to set
	 */
	public void setJavaName(String javaName) {
		this.javaName = javaName;
	}

	/**
	 * 获取dbType
	 *	@return the dbType
	 */
	public String getDbType() {
		return dbType;
	}

	/**
	 * 设置dbType
	 * @param dbType the dbType to set
	 */
	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	/**
	 * 获取javaType
	 *	@return the javaType
	 */
	public String getJavaType() {
		return javaType;
	}

	/**
	 * 设置javaType
	 * @param javaType the javaType to set
	 */
	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}

	/**
	 * 获取tableFieldAnno
	 *	@return the tableFieldAnno
	 */
	public String getTableFieldAnno() {
		return tableFieldAnno;
	}

	/**
	 * 设置tableFieldAnno
	 * @param tableFieldAnno the tableFieldAnno to set
	 */
	public void setTableFieldAnno(String tableFieldAnno) {
		this.tableFieldAnno = tableFieldAnno;
	}
	
}
