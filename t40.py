from t39 import invmode, int_to_bytes, gcd, lcm, encrypt
from Crypto.Util.number import getPrime


def find_cube_root(n):
    lo = 0
    hi = n

    while lo < hi:
        mid = (lo + hi) // 2
        if mid ** 3 < n:
            lo = mid + 1
        else:
            hi = mid

    return lo


def rsa_broadcast_attack(texts):
    c0, c1, c2 = texts[0][0], texts[1][0], texts[2][0]
    n0, n1, n2 = texts[0][1], texts[1][1], texts[2][1]
    m0, m1, m2 = n1 * n2, n0 * n2, n0 * n1

    t0 = (c0 * m0 * invmode(m0, n0))
    t1 = (c1 * m1 * invmode(m1, n1))
    t2 = (c2 * m2 * invmode(m2, n2))
    c = (t0 + t1 + t2) % (n0 * n1 * n2)

    return int_to_bytes(find_cube_root(c))


plaintext = b"Message message message message"

texts = []
for _ in range(3):
    e = 3
    key_length = 1024
    phi = 0

    while gcd(e, phi) != 1:
        p, q = getPrime(key_length // 2), getPrime(key_length // 2)
        phi = lcm(p - 1, q - 1)
        n = p * q

    d = invmode(e, phi)
    texts.append((encrypt(plaintext, e, n), n))

assert rsa_broadcast_attack(texts) == plaintext
