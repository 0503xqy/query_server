package com.xqy.service.impl.handler;

import com.xqy.dto.QueryNodeTreeDto;
import com.xqy.enums.QueryNodeType;
import com.xqy.service.DynamicDataSourceExecutor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 节点执行器
 *
 * @author xqy
 */
public class NodeExecutor {

    public static void execute(List<Map<String, Object>> baseResult, QueryNodeTreeDto queryNode, DynamicDataSourceExecutor dataSourceExecutor, Map<String, Object> params) {
        if (!queryNode.getHasChildren() || queryNode.getChildrenList() == null || queryNode.getChildrenList().isEmpty()){
            return;
        }
        List<QueryNodeTreeDto> childrenList = queryNode.getChildrenList();

        for (QueryNodeTreeDto child : childrenList) {
            for (Map<String, Object> row : baseResult){
                Map<String, Object> childParams = new HashMap<>(row);
                childParams.putAll(params);

                List<Map<String, Object>> childExecuteResult = dataSourceExecutor.executeQueryForList(child.getDataSourceId(), child.getSqlContent(), childParams);
                // 节点执行结果
                Object childExecuteResultValue = switch (child.getQueryNodeType()) {
                    case ROWS -> childExecuteResult;
                    case VALUE ->
                        // 取第一行第一列的值
                            childExecuteResult.get(0).values().iterator().next();
                    case COLUMN -> childExecuteResult.stream()
                            .map(obj -> obj.values().iterator().next())
                            .toList();
                    case ROW ->
                            childExecuteResult.get(0);
                };
                // 关系处理
                switch (child.getRelationType()){
                    case CHILD:
                        row.put(child.getBindingName(), childExecuteResultValue);
                        break;
                    case SIBLING:
                        if (child.getQueryNodeType().equals(QueryNodeType.ROW) && childExecuteResult instanceof Map<?,?>){
                            row.putAll((Map) childExecuteResultValue);
                        }
                        break;
                }
                // 递归处理
                if (List.of(QueryNodeType.ROWS, QueryNodeType.ROW).contains(child.getQueryNodeType())){
                    execute(childExecuteResult, child, dataSourceExecutor, childParams);
                }
            }

        }

    }

}
