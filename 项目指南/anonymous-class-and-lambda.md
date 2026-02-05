# 匿名类（Anonymous Class）与 Lambda：定义方式、概念与应用对比（Java）

本文梳理：
1) 匿名类的两种常见“定义/使用”方式  
2) Lambda 的基本概念（为何可省略参数类型）  
3) Lambda 与匿名类的应用对比与选择建议

---

## 1. 匿名类是什么

匿名类是 **没有类名的局部内部类**，通常用于“只用一次”的实现：  
在创建对象（`new`）的表达式位置**临时声明一个类**，并立刻实例化。

匿名类可以：
- 实现一个接口（`new Interface(){...}`）
- 继承一个类（`new Parent(){...}`）

匿名类不能声明构造器（没有类名），但可以有字段、方法、实例初始化块等。

---

## 2. 匿名类的两种定义方式

### 2.1 方式一：实现接口（interface）

**语法：**
```java
SomeInterface obj = new SomeInterface() {
    @Override
    public void method() {
        // ...
    }
};
```

**示例（Runnable）：**
```java
Runnable task = new Runnable() {
    @Override
    public void run() {
        System.out.println("Hello from anonymous class");
    }
};
task.run();
```

适用：需要一个接口实现，但不想单独写命名类。

---

### 2.2 方式二：继承父类（extend class）

**语法：**
```java
Parent obj = new Parent() {
    @Override
    public void someMethod() {
        // ...
    }
};
```

**示例（Thread）：**
```java
Thread t = new Thread() {
    @Override
    public void run() {
        System.out.println("Thread running");
    }
};
t.start();
```

适用：需要覆盖父类方法定制行为，但只用一次。

---

## 3. 匿名类访问外部变量的关键点

- 匿名类可访问外部类（enclosing class）的成员（字段/方法）。
- 访问方法内局部变量时，局部变量必须是：
  - `final`，或
  - **effectively final（有效 final）**：赋值后不再更改

示例：
```java
void f() {
    int x = 10; // effectively final
    Runnable r = new Runnable() {
        public void run() {
            System.out.println(x); // OK
        }
    };
    // x = 11; // 一旦改动，编译报错
}
```

---

## 4. Lambda 的基本概念

Lambda 是 Java 8 引入的语法糖，用于 **函数式接口**（Functional Interface）的实现。

**函数式接口**：只包含 **一个抽象方法** 的接口（可含 default/static 方法）。  
典型：`Runnable`、`Comparator<T>`、`Predicate<T>`、`Function<T,R>`、`Consumer<T>` 等。

### 4.1 基本语法

```java
(parameters) -> expression
(parameters) -> { statements; }
```

示例：
```java
Runnable r = () -> System.out.println("run");
Comparator<String> c = (a, b) -> a.length() - b.length();
```

### 4.2 为什么 lambda 参数经常不写类型

因为 Java 有 **目标类型（target type）** 推断：
- 变量/参数声明已经给出了函数式接口类型
- 编译器据此推断参数类型与返回类型

例如：
```java
Function<String, Integer> f = s -> s.length();
// s 的类型由 Function<String,Integer> 推断为 String
```

---

## 5. Lambda 与匿名类：核心差异

### 5.1 适用前提不同

- 匿名类：可实现接口、可继承类；接口不要求只有一个方法
- Lambda：**只能用于函数式接口**（只有一个抽象方法）

因此下面接口不能直接用 lambda（因为有两个抽象方法）：
```java
interface HelloWorld {
    void greet();
    void greetSomeone(String someone);
}
```

### 5.2 `this` 指向不同

- 匿名类中的 `this`：指匿名类实例本身
- lambda 中的 `this`：指外部类实例（lambda 不引入新的 `this`）

### 5.3 代码风格差异

- 匿名类：更冗长，但表达力强（可写字段/多方法/初始化块）
- lambda：更简洁，适合短逻辑，常配合 Stream API

---

## 6. 同一场景下的两种写法对照

### 6.1 Runnable：匿名类 vs lambda

**匿名类：**
```java
Runnable r = new Runnable() {
    @Override public void run() {
        System.out.println("run");
    }
};
```

**lambda：**
```java
Runnable r = () -> System.out.println("run");
```

---

### 6.2 Comparator：匿名类 vs lambda

**匿名类：**
```java
Comparator<String> c = new Comparator<>() {
    @Override
    public int compare(String a, String b) {
        return a.length() - b.length();
    }
};
```

**lambda：**
```java
Comparator<String> c = (a, b) -> a.length() - b.length();
```

---

## 7. 常见应用场景建议

### 7.1 更适合 lambda 的场景
- 集合排序：`list.sort((a,b) -> ...)`
- Stream 操作：`map/filter/forEach`
- 简短回调：`Runnable/Consumer/Predicate/Function`

### 7.2 更适合匿名类的场景
- 需要实现 **多方法接口**
- 需要额外字段/辅助方法/初始化逻辑
- 需要继承某个具体类并覆盖方法

---

## 8. 选择口诀（实践导向）

- **“一个方法的接口”** → 优先 lambda（更简洁）
- **“多方法/要状态/要继承”** → 用匿名类或命名类（更清晰、更可维护）

---
