package org.lars.commons.queries.builders;

import org.lars.commons.queries.Query;
import org.lars.commons.queries.Stretch;
import org.lars.commons.queries.Where;

import java.util.ArrayList;

public class SelectBuilder {
    public String buildSelect(ArrayList<String> columns, boolean deep, ArrayList<Stretch> stretches, String tableName,ArrayList<Where> wheres,boolean setAsOr,String orderBy,String limit,String offset){
        StringBuilder columnsBuilder=new StringBuilder();
        StringBuilder joinBuilder=new StringBuilder();
        for(String column:columns){
            columnsBuilder.append(",");
            columnsBuilder.append("r.").append(column.replace(" ", "")).append(" ");
            columnsBuilder.append("r_").append(column.replace(" ", ""));
        }
        if(deep){
            for (Stretch stretch:stretches) {
                if(stretch.getExtensionType()== Query.one){
                    for(String column:stretch.getColumns()){
                        columnsBuilder.append(",")
                                .append("r").append(stretch.getTableAliasId()).append(".")
                                .append(column.replace(" ",""))
                                .append(" ").append("r").append(stretch.getTableAliasId()).append("_")
                                .append(column.replace(" ",""));
                    }
                    joinBuilder.append(" JOIN ")
                            .append(stretch.getTableName())
                            .append(" r").append(stretch.getTableAliasId())
                            .append(" ON ")
                            .append("r.").append(stretch.getLocalKey())
                            .append("=r").append(stretch.getTableAliasId()).append(".")
                            .append(stretch.getForeignKey())
                            .append(" ");
                }
            }
        }
        columnsBuilder.deleteCharAt(0);
        String whereStatement=buildWhere(wheres,setAsOr);
        StringBuilder sqlBuilder=new StringBuilder();
        sqlBuilder.append("SELECT ")
                .append(columnsBuilder)
                .append(" FROM ")
                .append(tableName)
                .append(" r")
                .append(joinBuilder);
        sqlBuilder.append(whereStatement);
        if(orderBy!=null){
            sqlBuilder.append(" ");
            sqlBuilder.append(orderBy);
        }
        if(limit!=null){
            sqlBuilder.append(" ");
            sqlBuilder.append(limit);
        }
        if(offset!=null){
            sqlBuilder.append(" ");
            sqlBuilder.append(offset);
        }
        return sqlBuilder.toString();
    }
    String buildWhere(ArrayList<Where> wheres,boolean setAsOr){
        StringBuilder whereStatement=new StringBuilder();
        if(wheres!=null){
            String separator;
            if(setAsOr){
                separator=" OR ";
            }else{
                separator=" AND ";
            }
            whereStatement.append(" WHERE ");
            for(int i=0;i<wheres.size();i++){
                Where where=wheres.get(i);
                if(i>0){
                    whereStatement.append(separator);
                }
                whereStatement.append("r.").append(where.getKey().replace(" ", ""));
                whereStatement.append(" ");
                whereStatement.append(where.getOperator());
                whereStatement.append(" ?");
                where.setId(i+1);
            }
        }
        return whereStatement.toString();
    }
}
