
local keyH = KEYS[1]
local ttl = KEYS[2]

if redis.call('GET', keyH) == '1'
then
    return false
else
    redis.call('SET', keyH, '1', 'EX', ttl)
    return true
end