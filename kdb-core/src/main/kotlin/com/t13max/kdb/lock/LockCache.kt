package com.t13max.kdb.lock

import ReentrantMutex
import com.t13max.kdb.utils.CoroutineReadWriteLock
import kotlinx.coroutines.sync.Mutex

/**
 * 锁缓存
 * 简单实现 未来可优化为弱引用并发集合
 *
 * @author t13max
 * @since 11:48 2025/7/9
 */
class LockCache {

    companion object {

        private val cacheMap = mutableMapOf<String, MutableMap<Long, RecordLock>>()

        //拿全局唯一锁的锁
        private val lock = Mutex()

        //刷库读写锁
        private val flushLock = CoroutineReadWriteLock()

        suspend fun getLock(name: String, id: Long): RecordLock {

            lock.lock()

            try {
                val innerMap = cacheMap.getOrPut(name) { mutableMapOf() }

                return innerMap.getOrPut(id) {
                    RecordLock(name, ReentrantMutex())
                }
            } finally {
                lock.unlock()
            }
        }

        fun flushLock(): CoroutineReadWriteLock {
            return flushLock
        }

    }

}