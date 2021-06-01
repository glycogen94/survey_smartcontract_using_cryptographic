from Crypto.Cipher import PKCS1_OAEP
from Crypto.PublicKey import RSA

if __name__ == '__main__':
    key = RSA.generate(2048)
    print("pk.n: ",key.n)
    print("pk.e: ",key.e)
    print("sk.d: ",key.d)
    print("sk.p: ",key.p)
    print("sk.q: ",key.q)
    print("sk.u: ",key.u)
