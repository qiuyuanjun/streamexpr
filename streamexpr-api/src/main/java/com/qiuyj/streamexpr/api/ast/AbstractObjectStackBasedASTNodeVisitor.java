package com.qiuyj.streamexpr.api.ast;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Stack;

/**
 * @author qiuyj
 * @since 2023-07-26
 */
@SuppressWarnings("unchecked")
public abstract class AbstractObjectStackBasedASTNodeVisitor implements ASTNodeVisitor {

    private final Stack<Object> objectStack = new Stack<>();

    protected AbstractObjectStackBasedASTNodeVisitor(Object root) {
        pushObject(root);
    }

    protected Object getRootObject() {
        Object rootObject = popObject();
        if (objectStack.isEmpty()) {
            return rootObject;
        }
        throw new IllegalStateException("");
    }

    protected <ParentObjectType, CurrentObjectType, AstType> void executeVisit(VisitAction<ParentObjectType, CurrentObjectType, AstType> visitAction, AstType astNode) {
        // 父对象出栈
        ParentObjectType parentObject = popObject();
        // 创建当前对象并入栈
        CurrentObjectType currentObject = visitAction.createCurrentObject(astNode);
        boolean needPushAndPopCurrentObject = Objects.nonNull(currentObject);
        if (needPushAndPopCurrentObject) {
            pushObject(currentObject);
        }
        // 执行访问节点代码
        visitAction.doVisit(parentObject, astNode);
        // 执行访问节点之后的代码
        visitAction.postVisit(parentObject,
                needPushAndPopCurrentObject ? popObject() : null,
                astNode);
        // 最后将父对象入栈
        pushObject(parentObject);
    }

    protected void pushObject(Object obj) {
        objectStack.push(obj);
    }

    protected <T> T popObject() {
        return (T) objectStack.pop();
    }

    public static abstract class VisitAction<ParentObjectType, CurrentObjectType, AstType> {

        /**
         * 默认实现，根据泛型参数{@code CurrentObjectType}实例化对应的对象
         * @apiNote 要求当前对象必须要有默认的构造函数
         * @return 当前对象
         */
        public CurrentObjectType createCurrentObject(AstType astNode) {
            ParameterizedType p = (ParameterizedType) getClass().getGenericSuperclass();
            Type[] typeArgs = p.getActualTypeArguments();
            if (typeArgs.length != 2) {
                throw new IllegalStateException("Must specify the parent object type parameter and current object type parameter!");
            }
            Class<CurrentObjectType> currentObjectType = (Class<CurrentObjectType>) typeArgs[1];
            try {
                return currentObjectType.getDeclaredConstructor().newInstance();
            }
            catch (Exception e) {
                throw new IllegalStateException("Can not create current object by default!", e);
            }
        }

        public abstract void doVisit(ParentObjectType parentObject, AstType astNode);

        public void postVisit(ParentObjectType parentObject, CurrentObjectType currentObject, AstType astNode) {
            // do nothing
        }
    }

}
