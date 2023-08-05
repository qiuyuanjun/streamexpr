/**
 * @author qiuyj
 * @since 2023-07-21
 */
module streamexpr.core {

    requires scala.library;
    requires transitive streamexpr.api;
    requires spring.expression;

    exports com.qiuyj.streamexpr;

    exports com.qiuyj.streamexpr.parser to streamexpr.test;
    exports com.qiuyj.streamexpr.utils to streamexpr.test;
}