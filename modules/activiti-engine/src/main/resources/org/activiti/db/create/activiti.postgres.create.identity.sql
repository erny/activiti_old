create table ${prefix}ACT_ID_GROUP (
    ID_ varchar(64),
    REV_ integer,
    NAME_ varchar(255),
    TYPE_ varchar(255),
    primary key (ID_)
);

create table ${prefix}ACT_ID_MEMBERSHIP (
    USER_ID_ varchar(64),
    GROUP_ID_ varchar(64),
    primary key (USER_ID_, GROUP_ID_)
);

create table ${prefix}ACT_ID_USER (
    ID_ varchar(64),
    REV_ integer,
    FIRST_ varchar(255),
    LAST_ varchar(255),
    EMAIL_ varchar(255),
    PWD_ varchar(255),
    PICTURE_ID_ varchar(64),
    primary key (ID_)
);

create table ${prefix}ACT_ID_INFO (
    ID_ varchar(64),
    REV_ integer,
    USER_ID_ varchar(64),
    TYPE_ varchar(64),
    KEY_ varchar(255),
    VALUE_ varchar(255),
    PASSWORD_ bytea,
    PARENT_ID_ varchar(255),
    primary key (ID_)
);

create index ${prefix}ACT_IDX_MEMB_GROUP on ${prefix}ACT_ID_MEMBERSHIP(GROUP_ID_);
alter table ${prefix}ACT_ID_MEMBERSHIP 
    add constraint ${prefix}ACT_FK_MEMB_GROUP
    foreign key (GROUP_ID_) 
    references ${prefix}ACT_ID_GROUP (ID_);

create index ${prefix}ACT_IDX_MEMB_USER on ${prefix}ACT_ID_MEMBERSHIP(USER_ID_);
alter table ${prefix}ACT_ID_MEMBERSHIP 
    add constraint ${prefix}ACT_FK_MEMB_USER
    foreign key (USER_ID_) 
    references ${prefix}ACT_ID_USER (ID_);
