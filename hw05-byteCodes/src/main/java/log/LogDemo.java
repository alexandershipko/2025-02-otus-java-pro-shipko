package log;

import log.proxy.LogProxyFactory;
import log.service.Calculator;
import log.service.MyClass;
import log.service.impl.CalculatorImpl;
import log.service.impl.MyClassImpl;


public class LogDemo {

    public static void main(String[] args) {
        //first case
        Calculator calculatorProxy = LogProxyFactory.createProxy(Calculator.class, new CalculatorImpl());

        calculatorProxy.calculation(6);
        calculatorProxy.calculation(1, 2);
        calculatorProxy.calculation(3, 4, "Hello");

        //disabled
        calculatorProxy.anotherMethod("Test");


        //second case
        MyClass myClassProxy = LogProxyFactory.createProxy(MyClass.class, new MyClassImpl());

        myClassProxy.testMethod();
    }

}