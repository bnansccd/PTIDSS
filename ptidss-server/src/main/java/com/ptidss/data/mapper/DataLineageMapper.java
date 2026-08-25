package com.ptidss.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ptidss.data.domain.DataLineage;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Update;

/**
 * 数据血缘（FR-PD-05；V3.0 全量图谱：节点中文名/说明/业务域/分层 schema 幂等迁移 + 全量种子物理重建）
 */
public interface DataLineageMapper extends BaseMapper<DataLineage> {

    /** V3.0 幂等加列：节点中文名 */
    @Update("ALTER TABLE data_lineage ADD COLUMN IF NOT EXISTS node_name VARCHAR(64)")
    void addColumnNodeName();

    /** V3.0 幂等加列：中文说明 */
    @Update("ALTER TABLE data_lineage ADD COLUMN IF NOT EXISTS description VARCHAR(255)")
    void addColumnDescription();

    /** V3.0 幂等加列：业务域 */
    @Update("ALTER TABLE data_lineage ADD COLUMN IF NOT EXISTS domain VARCHAR(32)")
    void addColumnDomain();

    /** V3.0 幂等加列：数据分层 */
    @Update("ALTER TABLE data_lineage ADD COLUMN IF NOT EXISTS layer VARCHAR(16)")
    void addColumnLayer();

    /** V3.0 幂等重建 node_type CHECK（扩展 business 业务应用节点） */
    @Update("ALTER TABLE data_lineage DROP CONSTRAINT IF EXISTS data_lineage_node_type_check")
    void dropNodeTypeCheck();

    @Update("ALTER TABLE data_lineage ADD CONSTRAINT data_lineage_node_type_check " +
            "CHECK (node_type IN ('table','task','report','model','business'))")
    void addNodeTypeCheck();

    /** 全量种子物理清空（血缘为配置数据，重建时清空旧 7 节点，避免部分唯一索引冲突） */
    @Delete("DELETE FROM data_lineage")
    void physicalClear();
}
