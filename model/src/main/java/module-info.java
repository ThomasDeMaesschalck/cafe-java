module be.hogent.cafe.model {
    requires java.sql;
    requires log4j.api;
    requires kernel;
    requires layout;
    requires jfreechart;
    exports be.hogent.cafe.model;
    exports be.hogent.cafe.model.dao;
}