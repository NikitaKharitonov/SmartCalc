package com.sct.smartcalc.graphs.expression;


import com.google.gson.InstanceCreator;

import java.lang.reflect.Type;

public class FunctionInstanceCreator implements InstanceCreator<Function> {

        Quantity root;
        Var x;

        public FunctionInstanceCreator(Quantity root, Var x) {
            this.root = root;
            this.x = x;
        }

        @Override
        public Function createInstance(Type type) {
            return new Function(root, x);
        }

}
