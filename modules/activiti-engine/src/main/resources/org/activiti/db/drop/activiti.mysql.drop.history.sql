drop index ${prefix}ACT_IDX_HI_PRO_INST_END on ${prefix}ACT_HI_PROCINST;
drop index ${prefix}ACT_IDX_HI_PRO_I_BUSKEY on ${prefix}ACT_HI_PROCINST;
drop index ${prefix}ACT_IDX_HI_ACT_INST_START on ${prefix}ACT_HI_ACTINST;
drop index ${prefix}ACT_IDX_HI_ACT_INST_END on ${prefix}ACT_HI_ACTINST;
drop index ${prefix}ACT_IDX_HI_DETAIL_PROC_INST on ${prefix}ACT_HI_DETAIL;
drop index ${prefix}ACT_IDX_HI_DETAIL_ACT_INST on ${prefix}ACT_HI_DETAIL;
drop index ${prefix}ACT_IDX_HI_DETAIL_TIME on ${prefix}ACT_HI_DETAIL;
drop index ${prefix}ACT_IDX_HI_DETAIL_NAME on ${prefix}ACT_HI_DETAIL;

drop table if exists ${prefix}ACT_HI_PROCINST;
drop table if exists ${prefix}ACT_HI_ACTINST;
drop table if exists ${prefix}ACT_HI_TASKINST;
drop table if exists ${prefix}ACT_HI_DETAIL;
drop table if exists ${prefix}ACT_HI_COMMENT;
drop table if exists ${prefix}ACT_HI_ATTACHMENT;
 