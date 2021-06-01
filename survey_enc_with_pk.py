from Crypto.Cipher import PKCS1_OAEP
from Crypto.PublicKey import RSA
from base64 import b64decode


if __name__ == '__main__':
    element1 = int(input("n: "))
    element2 = int(input("e: "))
    pk = RSA.RsaKey(n = element1, e = element2)
    encryptor = PKCS1_OAEP.new(pk)
    print(pk)
    plain_text = input("plain text: ")
    cipher_text = encryptor.encrypt(plain_text.encode())
    print(cipher_text)