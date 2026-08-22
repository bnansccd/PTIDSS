package com.troy.common.core.utils;

import com.troy.common.core.web.VO.NodeVO;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Classname: IterateUtils
 * @Description:
 * @Date 2022/9/11
 * @Author: yzy
 * @Version
 **/
public class IterateUtils {

    /**
     * @param list 所有数据
     * @return
     * @author yzy
     * @description 将根节点和子节点分开
     * @date 2022/9/11
     * @version
     */

    private  static <T extends NodeVO> Set<T> getRoot(List<T> list){
        Set<T> roots = new HashSet<>();
        HashMap<Long, T> hashMap = new HashMap<>();
        list.forEach(e-> hashMap.put(e.getId(), e));

        list.forEach(e->{
            T tmp = e;
            while (hashMap.containsKey(tmp.getParentId())){
                tmp = hashMap.get(tmp.getParentId());
            }
            roots.add(tmp);
        });
        return roots;
    }

    public static <T extends NodeVO> List<T> getList(List<T> list) {
        List<T> roots = new ArrayList<>();
        List<T> subs = new ArrayList<>();
        if (StringUtils.isNotEmpty(list)){
            roots = new ArrayList<>(getRoot(list));
            list.removeAll(roots);
            for (T root : roots) {
                iterate(list, root);
            }
        }
        return roots;
    }

    /**
     * 递归遍历
     *
     * @param subs   所有子节点
     * @param parent 当前父节点
     */
    private static <T extends NodeVO> T iterate(List<T> subs, T parent) {
        List<T> children = new ArrayList<>();
        for (T child : subs) {
            if (parent.getId().equals(child.getParentId())) {
                children.add(iterate(subs, child));
            }
        }
        parent.setChildren(children);
        return parent;
    }

    /**
     * 将列表转换为树形结构
     * @param nodes 所有节点列表
     * @return 根节点列表
     */
    public static <T extends NodeVO<T>> List<T> buildTree(List<T> nodes) {
        if (StringUtils.isEmpty(nodes)) {
            return new ArrayList<>();
        }

        // 创建映射，方便查找节点
        Map<Long, T> nodeMap = nodes.stream()
                .collect(Collectors.toMap(NodeVO::getId, node -> node, (v1, v2) -> v1));

        List<T> tree = new ArrayList<>();

        for (T node : nodes) {
            Long parentId = node.getParentId();
            // 如果parentId为null或0，或者不存在父节点，则为根节点
            if (parentId == null || parentId == 0 || !nodeMap.containsKey(parentId)) {
                tree.add(node);
            } else {
                // 找到父节点，将当前节点添加到父节点的children中
                T parent = nodeMap.get(parentId);
                if (parent.getChildren() == null) {
                    parent.setChildren(new ArrayList<>());
                }
                parent.getChildren().add(node);
            }
        }

        // 对每个节点的子节点进行排序
        for (T node : tree) {
            sortChildren(node);
        }

        return tree;
    }

    /**
     * 递归排序子节点
     */
    private static <T extends NodeVO<T>> void sortChildren(T node) {
        if (StringUtils.isEmpty(node.getChildren())) {
            return;
        }

        // 根据sort字段排序
        List<T> sortedChildren = node.getChildren().stream()
                .sorted(Comparator.comparing(NodeVO::getSort, Comparator.nullsLast(Integer::compareTo)))
                .collect(Collectors.toList());

        node.setChildren(sortedChildren);

        // 递归排序子节点的子节点
        for (T child : sortedChildren) {
            sortChildren(child);
        }
    }

}
