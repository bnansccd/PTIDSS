package com.troy.common.redis.service;

import com.troy.common.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Auther: zhuqing
 * @Date: 2022/7/29 14:14:04
 * @Description: spring redis 工具类
 * @Version: 1.0.0
 */
@Component
public class RedisService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     */
    public <T> void setCacheObject(final String key, final T value, final Long timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 获取有效时间
     *
     * @param key Redis键
     * @return 有效时间
     */
    public long getExpire(final String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 判断 key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final String key) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public boolean deleteObject(final String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     * @return
     */
    public long deleteObject(final Collection collection) {
        return redisTemplate.delete(collection);
    }

    /**
     * 缓存List数据
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public <T> long setCacheList(final String key, final List<T> dataList) {
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCacheList(final String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> BoundSetOperations<String, T> setCacheSet(final String key, final Set<T> dataSet) {
        BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
        Iterator<T> it = dataSet.iterator();
        while (it.hasNext()) {
            setOperation.add(it.next());
        }
        return setOperation;
    }

    /**
     * 获得缓存的set
     *
     * @param key
     * @return
     */
    public <T> Set<T> getCacheSet(final String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    /**
     * 获得缓存的Map
     *
     * @param key
     * @return
     */
    public <T> Map<String, T> getCacheMap(final String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 往Hash中存入数据
     *
     * @param key   Redis键
     * @param hKey  Hash键
     * @param value 值
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value) {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    /**
     * 获取Hash中的数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public <T> T getCacheMapValue(final String key, final String hKey) {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, hKey);
    }

    /**
     * 获取多个Hash中的数据
     *
     * @param key   Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys) {
        return redisTemplate.opsForHash().multiGet(key, hKeys);
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public Set<String> keys(final String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 通过一批key批量查询
     *
     * @param keys
     * @return
     */
    public List<Object> multiGet(Set<String> keys) {
        try {
            return redisTemplate.opsForValue().multiGet(keys);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * geoJson点位匹配
     *
     * @param key
     * @param lot
     * @param lat
     * @param limit
     * @param len
     * @return
     */
    public List getGeo(String key, double lot, double lat, int limit, double len) {
        Circle circle = new Circle(new Point(lot, lat), new Distance(len, RedisGeoCommands.DistanceUnit.METERS));
        //设置所要返回的信息
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                //设置数量
                .limit(limit)
                //包含成员的坐标信息
                .includeCoordinates()
                //包含成员距离中心点的距离
                .sortAscending()
                .includeDistance();

        GeoResults<RedisGeoCommands.GeoLocation> radius = this.redisTemplate.opsForGeo().radius(key, circle, args);
        if (radius != null && StringUtils.isNotEmpty(radius.getContent())) {
            return radius.getContent().stream().map(e -> e.getContent().getName()).collect(Collectors.toList());
        } else {
            return null;
        }
    }


    public List<GeoResult<RedisGeoCommands.GeoLocation>> getGeoWithDistance(String key, double lot, double lat, int limit, double len){
        Circle circle = new Circle(new Point(lot, lat), new Distance(len, RedisGeoCommands.DistanceUnit.METERS));
        //设置所要返回的信息
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                //设置数量
                .limit(limit)
                //包含成员距离中心点的距离
                .sortAscending()
                .includeDistance();

        GeoResults<RedisGeoCommands.GeoLocation> radius = this.redisTemplate.opsForGeo().radius(key, circle, args);
        if (radius != null && StringUtils.isNotEmpty(radius.getContent())) {
            return radius.getContent();
        } else {
            return null;
        }
    }

    /**
     * 往redis里面添加gps点位
     *
     * @param key
     * @param point
     * @param object
     */
    public void putGeo(String key, Point point, Object object) {
        this.redisTemplate.opsForGeo().add(key, point, object);
    }

    public Long removeGeo(String key, Object... object) {
        return this.redisTemplate.opsForGeo().remove(key, object);
    }

    /**
     * 记数器
     *
     * @param key
     * @return
     */
    public Long incrementCounter(String key) {
        return stringRedisTemplate.opsForValue().increment(key);
    }

    /**
     * 获取计算器中最大数
     *
     * @param key
     * @return
     */
    public Long getCounter(String key) {
        String value = stringRedisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value) : 0L;
    }

    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public Long increase(String key, Long delta){
        return redisTemplate.boundValueOps(key).increment(delta);
    }

    public Long decrease(String key, Long delta){
        return redisTemplate.boundValueOps(key).decrement(delta);
    }
}
