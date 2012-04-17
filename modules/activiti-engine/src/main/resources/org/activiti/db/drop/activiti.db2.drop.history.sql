drop index ${prefix}ACT_IDX_HI_PRO_INST_END;
drop index ${prefix}ACT_IDX_HI_PRO_I_BUSKEY;
drop index ${prefix}ACT_IDX_HI_ACT_INST_START;
drop index ${prefix}ACT_IDX_HI_ACT_INST_END;
drop index ${prefix}ACT_IDX_HI_DETAIL_PROC_INST;
drop index ${prefix}ACT_IDX_HI_DETAIL_ACT_INST;
drop index ${prefix}ACT_IDX_HI_DETAIL_TIME;
drop index ${prefix}ACT_IDX_HI_DETAIL_NAME;

drop table ${prefix}ACT_HI_PROCINST;
drop table ${prefix}ACT_HI_ACTINST;
drop table ${prefix}ACT_HI_TASKINST;
drop table ${prefix}ACT_HI_DETAIL;
drop table ${prefix}ACT_HI_COMMENT;
drop table ${prefix}ACT_HI_ATTACHMENT;
