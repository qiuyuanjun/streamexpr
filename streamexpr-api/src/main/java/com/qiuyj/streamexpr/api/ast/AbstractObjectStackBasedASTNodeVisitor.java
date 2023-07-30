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

    /**
     * 得到根对象
     * @apiNote 当前方法调用，必须放在最后，等所有其他的对象都出栈了之后再调用
     * @return 根对象
     */
    protected Object getRootObject() {
        Object rootObject = popObject();
        if (objectStack.isEmpty()) {
            return rootObject;
        }
        throw new IllegalStateException("The object obtained in the current call state is not root object");
    }

    protected <ParentObjectType, CurrentObjectType, AstType> void executeVisit(
            VisitAction<ParentObjectType, CurrentObjectType, AstType> visitAction,
            AstType astNode) {
        // 获取父对象（不能把父对象出栈，因为后续递归需要使用）
        ParentObjectType parentObject = peekObject();
        // 创建当前对象并入栈
        CurrentObjectType currentObject = visitAction.createCurrentObject(astNode);
        boolean needPushAndPopCurrentObject = Objects.nonNull(currentObject);
        if (needPushAndPopCurrentObject) {
            pushObject(currentObject);
        }
        // 执行访问节点代码
        visitAction.doVisit(parentObject, currentObject, astNode);
        // 执行访问节点之后的代码
        visitAction.postVisit(parentObject,
                needPushAndPopCurrentObject ? popObject() : null,
                astNode);
    }

    /**
     * 入栈
     * @param obj 要入栈的对象
     */
    private void pushObject(Object obj) {
        objectStack.push(obj);
    }

    /**
     * 弹出栈顶对象
     * @return 栈顶对象
     */
    private <T> T popObject() {
        return (T) objectStack.pop();
    }

    /**
     * 获取栈顶对象，但是不出栈
     * @return 栈顶对象
     */
    private <T> T peekObject() {
        return (T) objectStack.peek();
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
            if (typeArgs.length < 2) {
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

        public void doVisit(ParentObjectType parentObject, CurrentObjectType currentObject, AstType astNode) {
            // do nothing
        }

        public void postVisit(ParentObjectType parentObject, CurrentObjectType currentObject, AstType astNode) {
            // do nothing
        }
    }

}
