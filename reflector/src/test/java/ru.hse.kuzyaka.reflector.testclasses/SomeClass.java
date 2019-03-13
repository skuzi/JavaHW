package ru.hse.kuzyaka.reflector.testclasses;

public class SomeClass <T extends java.lang.Object, E extends java.util.List<T>> {
    T field1;
    E field2;
    
     <B extends java.lang.Object> SomeClass(B arg0, T arg1) {
        
    }
    
     SomeClass(T arg0) {
        
    }
    
    public T genericMethod(java.util.List<? super E> arg0, java.util.Map<T, ? extends T> arg1) {
        return null;
    }
    
    
    private class Inner extends java.util.AbstractList<E> {
        
        private Inner() {
            
        }
        
        public E get(int arg0) {
            return null;
        }
        
        public int size() {
            return 0;
        }
        
    }
    
    static class genericNestedClass <B extends java.lang.Object> {
        B field1;
        
         genericNestedClass() {
            
        }
        
    }
}
