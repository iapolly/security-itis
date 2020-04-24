import random as rd

P = int('ffffffffffffffffc90fdaa22168c234c4c6628b80dc1cd129024e088a67cc74020bbea63b139b225'
        '14a08798e3404ddef9519b3cd3a431b302b0a6df25f14374fe1356d6d51c245e485b576625e7ec6f4'
        '4c42e9a637ed6b0bff5cb6f406b7edee386bfb5a899fa5ae9f24117c4b1fe649286651ece45b3dc20'
        '07cb8a163bf0598da48361c55d39a69163fa8fd24cf5f83655d23dca3ad961c62f356208552bb9ed5'
        '29077096966d670c354e4abc9804f1746c08ca237327ffffffffffffffff', 16)
G = 2


def get_random_key(p):
    return rd.randint(0, p - 1)


def get_key(g, random_key, p):
    return modular_pow(g, random_key, p)


def modular_pow(base, exponent, modulus):
    if modulus == -1:
        return 0

    result = 1
    base %= modulus

    while exponent > 0:
        if exponent % 2:
            result = (result * base) % modulus
        exponent >>= 1
        base = (base * base) % modulus

    return result


a = get_random_key(P)
b = get_random_key(P)

public_A = get_key(G, a, P)
public_B = get_key(G, b, P)

private_A = get_key(public_B, a, P)
private_B = get_key(public_A, b, P)

assert private_A == private_B
