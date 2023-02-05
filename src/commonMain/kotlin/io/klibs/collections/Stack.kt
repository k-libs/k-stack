package io.klibs.collections

import kotlin.math.max
import kotlin.math.min

/**
 * Creates a new [Stack] instance wrapping the given values.
 *
 * The first of the given [items] will be the top of the stack, and the last of
 * the given items will be the bottom.
 *
 * Example:
 * ```
 * val stack = stackOf(1, 2, 3)
 *
 * stack.pop() // 1
 * stack.pop() // 2
 * stack.pop() // 3
 * ```
 *
 * @param items Items that will back the newly created stack.
 *
 * @return A new `Stack` instance.
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 * @since 0.1.0
 */
@Suppress("UNCHECKED_CAST")
fun <T> stackOf(vararg items: T): Stack<T> {
  items.reverse()
  return Stack(items as Array<Any?>)
}

/**
 * Creates a new [Stack] instance wrapping the values provided by the given
 * iterable.
 *
 * The first item provided by the [items] iterable will be the top of the stack,
 * and the last item provided by the iterable will be the bottom.
 *
 * Example:
 * ```
 * val items = listOf(1, 2, 3)
 * val stack = stackOf(items)
 *
 * stack.pop() // 1
 * stack.pop() // 2
 * stack.pop() // 3
 * ```
 *
 * @param items Items that will back the newly created stack.
 *
 * @return A new `Stack` instance.
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 * @since 0.1.0
 */
fun <T> stackOf(items: Iterable<T>): Stack<T> {
  val tmp: Array<Any?> = items.toList().toTypedArray()
  tmp.reverse()
  return Stack(tmp)
}


/**
 * # Generic Stack
 *
 * A simple stack implementation.
 *
 * Example:
 * ```
 * // Create a new stack
 * val stack = Stack<Int>()
 *
 * // Push 3, 2, then 1 onto the stack.
 * for (i in 3 downTo 1)
 *   stack.push(i)
 *
 * // Pop 1, 2, then 3 from the stack.
 * for (i in 1 .. 3)
 *   require(stack.pop() == i)
 * ```
 *
 * @param T Type of items that will be pushed onto this stack.
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 * @since 0.1.0
 */
class Stack<T> internal constructor(

  /**
   * Container for the items in the stack.
   */
  private var buffer: Array<Any?>,

  /**
   * Resize scale factor.
   */
  val scaleFactor: Float = 1.5F,

  /**
   * Max capacity this stack will permit.
   */
  val maxSize: Int = Int.MAX_VALUE,
) {

  /**
   * Current number of items currently on the stack.
   */
  var size: Int = 0
    private set

  /**
   * Current capacity of the stack's underlying data container.
   */
  var capacity: Int = buffer.size
    private set

  /**
   * `true` if this stack contains zero items.
   *
   * @since 0.3.0
   */
  inline val isEmpty get() = size == 0

  /**
   * `true` if this stack contains one or more items.
   *
   * @since 0.3.0
   */
  inline val isNotEmpty get() = size != 0

  /**
   * Last valid index in this stack.
   *
   * @since 0.3.0
   */
  inline val lastIndex get() = size - 1

  init {
    if (scaleFactor <= 1)
      throw IllegalArgumentException("scaleFactor given ($scaleFactor) was less than or equal to 1")
    if (maxSize < 0)
      throw IllegalArgumentException("maxSize given ($maxSize) was less than 0")
    if (maxSize < buffer.size)
      throw IllegalArgumentException("maxSize given ($maxSize) was less than the given initialSize (${buffer.size})")
  }

  /**
   * Constructs a new [Stack] instance.
   *
   * @param initialCapacity Initial capacity for the stack.
   *
   * Default: `8`
   *
   * @param scaleFactor Factor by which the stack's capacity will be increased
   * at a time when required to contain pushed values.
   *
   * When a value is pushed onto the stack that would increase the stack's
   * [size] to be greater than the stack's [capacity], the data container
   * underlying this stack will be resized to increase the capacity at a rate of
   * `capacity * scaleFactor`.
   *
   * Default: `1.5`
   *
   * @param maxSize Size cap for the stack.  If an attempt is made to push a
   * number of items into this stack that is greater than the [maxSize] value,
   * an exception will be thrown.
   *
   * Default: `2147483647`
   */
  constructor(
    initialCapacity: Int   = 8,
    scaleFactor:     Float = 1.5F,
    maxSize:         Int   = Int.MAX_VALUE
  ) : this(arrayOfNulls(initialCapacity), scaleFactor, maxSize)

  /**
   * Pushes the given item onto the top of this stack.
   *
   * If this stack's size is already equal to [maxSize], an exception will be
   * thrown.
   *
   * If this stack's size is already equal to [capacity], the stack's underlying
   * data container will be resized to accommodate new values by the configured
   * scaling factor.
   *
   * @param value Value to push onto the top of the stack.
   *
   * @throws IllegalStateException If pushing a new element onto this stack
   * would increase it's size to be greater than [maxSize].
   */
  fun push(value: T) {
    if (size == maxSize)
      throw IllegalStateException()

    if (size == capacity) {
      buffer   = buffer.copyOf(min(maxSize, max(capacity + 1, (capacity * scaleFactor).toInt())))
      capacity = buffer.size
    }

    buffer[size++] = value
  }

  /**
   * Removes and returns the item currently on top of this stack.
   *
   * If this stack is empty, an exception will be thrown.
   *
   * @return The item removed from the top of this stack.
   *
   * @throws NoSuchElementException If this stack is empty.
   */
  @Suppress("UNCHECKED_CAST")
  fun pop(): T {
    if (size < 1)
      throw NoSuchElementException()

    return buffer[--size].also { buffer[size] = null } as T
  }

  /**
   * Returns the item on the top of this stack without removing it.
   *
   * If this stack is empty, an exception will be thrown.
   *
   * @return The item currently at the top of this stack.
   *
   * @throws NoSuchElementException If this stack is empty.
   */
  @Suppress("UNCHECKED_CAST")
  fun peek(): T {
    if (size < 1)
      throw NoSuchElementException()

    return buffer[size - 1] as T
  }

  /**
   * Operator function allowing array style access of the stack.
   *
   * @param index Zero-based index of the stack item to get.
   *
   * @return The target stack item.
   *
   * @throws IndexOutOfBoundsException If the given index is less than zero or
   * greater than or equal to [size].
   *
   * @since 0.2.0
   */
  @Suppress("UNCHECKED_CAST")
  operator fun get(index: Int): T = buffer[internalIndex(index)] as T

  /**
   * Returns a string representation of this stack containing the stack's
   * current size.
   *
   * For example, given a stack instance containing 4 items, the output of this
   * method would be:
   * ```
   * Stack(size=4)
   * ```
   *
   * @return String representation of this stack.
   */
  override fun toString() = "Stack(size=$size)"

  /**
   * Returns whether this stack equals the given other value.
   *
   * For two stacks to be considered equal, their sizes, capacities, and
   * contents must all be equal and appear in the same order.
   *
   * @param other Other value to test this stack against.
   *
   * @return Whether this stack is equal to the given value.
   */
  override fun equals(other: Any?) = other is Stack<*> && other.buffer.contentEquals(buffer)

  /**
   * Returns the hash code value for this stack.
   *
   * @return The hash code value for this stack.
   */
  override fun hashCode() = 420 + buffer.contentHashCode()


  /**
   * Returns `true` if this stack contains the given [element].
   *
   * @return `true` if this stack contains the given [element].
   */
  operator fun contains(element: T) = buffer.contains(element)

  /**
   * Returns `true` if this stack contains all the given [elements].
   *
   * @return `true` if this stack contains all the given [elements].
   */
  fun containsAll(elements: Collection<T>) = elements.all { contains(it) }

  /**
   * Removes all items from this stack.
   *
   * The stack's capacity will not be changed.
   *
   * @since 0.2.0
   */
  fun clear() {
    while (size > 0)
      buffer[--size] = null
  }

  /**
   * Returns a new, destructive, consuming iterator over this stack's contents
   * that pops items from this stack as it is iterated.
   *
   * @return A new iterator.
   *
   * @since 0.3.0
   */
  fun destructiveIterator() = object {
    operator fun hasNext() = size > 0
    operator fun next() = pop()
  }

  /**
   * Returns a new, non-destructive iterator over this stack's contents.
   *
   * Unlike the iterator returned by [destructiveIterator], usage of this
   * iterator will not change the state of the source [Stack].
   *
   * @return A new iterator.
   *
   * @since 0.3.0
   */
  fun nonDestructiveIterator() = object {
    private var pos = 0
    operator fun hasNext() = pos < lastIndex
    @Suppress("UNCHECKED_CAST")
    operator fun next() = buffer[internalIndex(pos++)] as T
  }

  /**
   * Returns an array copy of the current contents of this [Stack] instance.
   *
   * **Example:**
   * ```
   * myStack.toArray(::Array<String>)
   * ```
   *
   * @return An array copy of the current contents of this [Stack] instance.
   *
   * @since 0.3.0
   */
  fun toArray(arrayProvider: (size: Int, init: (Int) -> T) -> Array<T>) = arrayProvider(size, ::get)

  /**
   * Pops the elements of this stack into an array.
   *
   * **Example:**
   * ```
   * myStack.toArray(::Array<String>)
   * ```
   *
   * @return The elements popped from this [Stack]
   *
   * @since 0.3.0
   */
  fun popToArray(arrayProvider: (size: Int, init: (Int) -> T) -> Array<T>) = arrayProvider(size) { pop() }

  private inline fun internalIndex(i: Int) = size - 1 - i
}