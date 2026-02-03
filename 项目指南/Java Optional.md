# Java Optional

`public final class Optional<T> extends Object`

**A container object which may or may not contain a non-null value.** If a value is present, `isPresent()` will return `true` and `get()` will return the value.

## of()

`public static <T> Optional<T> of (T value)`

**Returns an Optional with the specified present non-null value.**

value must be non-null or it will throw **NullPointerException.**

```java
Optional<Integer> optional1 = Optional.of(1);
```

## ofNullable()

`public static <T> Optional<T> ofNullable(T value)`

Returns an Optional describing the specified value, if non-null, otherwise returns an empty **Optional**.

```java
// 参数是null
Optional<Integer> optional2 = Optional.ofNullable(null);

// 参数不是null
Optional<Integer> optional3 = Optional.ofNullable(2);
```

## get()

`public T get()`

If a value is present in this Optional, returns the value, otherwise throws NoSuchElementException.

**Returns the non-null value held by this Optional.**

**value must be non-null or it will throw NoSuchElementException.**

## isPresent()

`public boolean isPresent()`

Returns **true** if there is a value present, otherwise **false**.

## orElse()

`public T orElse(T other)`

Return the value if present, otherwise return **other**.

```java
System.out.println(Optional.ofNullable("yes").orElse("no"));
System.out.println(Optional.ofNullable(null).orElse("no"));
```

Output:

```java
yes
no
```

## orElseGet()

`public T orElseGet(Supplier<? extends T> other)`

Return the value if present, otherwise invoke other and return the result of that invocation.

**Returns the value if present otherwise the result of other.get()**

Throws:

NullPointerException - if value is not present and other is null.

```java
System.out.println(Optional.ofNullable("yes").orElseGet(() -> "no"));
System.out.println(Optional.ofNullable(null).orElseGet(() -> "no"));
```

Output:

```java
yes
no
```

## orElseThrow()

`public <X entends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X`

Return the contained value, if present, otherwise throw an exception to be created by the provided supplier.

```java
// 有钱就没异常
try {  System.out.println(Optional.ofNullable("钱").orElseThrow(()->new Exception()));  // 有钱不会抛异常
} catch (Throwable throwable) {
    throwable.printStackTrace();
}

// 没钱就会抛异常
try { 		 	System.out.println(Optional.ofNullable(null).orElseThrow(()->new Exception()));  // 没钱抛异常
} catch (Throwable throwable) {
    throwable.printStackTrace();
}
```

Output:

```java
钱
java.lang.Exception
at Main.lambda$main$0(Main.java:5)
at java.base/java.util.Optional.orElseThrow(Optional.java:408)
at Main.main(Main.java:5)
```

