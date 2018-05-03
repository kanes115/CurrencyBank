import sys, Ice
import Bank

def welcome_message(name, lastname):
    print('*************')
    print('Hello' + name + ' ' + lastname)
    print('*************')


with Ice.initialize(sys.argv) as communicator:
    base = communicator.stringToProxy("SimplePrinter:default -p 10000")
    bank = Bank.BankServicePrx.checkedCast(base)
    if not bank:
        raise RuntimeError("Invalid proxy")

    # uid = bank.createAccount("Dominik", "Stanaszek", 96022882937, 2000000)
    # account = bank.getAccount(uid)
    # print(account.getBalance())

    accounts = {}

    lastname = None

    while True:
        command = input('>> ')
        if command.startswith('register'):
            [_register, name, lastname, pesel, income] = command.split()
            uid = bank.createAccount(name, lastname, int(pesel), int(income))
            accounts[lastname] = uid
            welcome_message(name, lastname)
        elif command == 'getBalance':
            if not lastname:
                print("Not logged in")
                continue
            account = bank.getAccount(accounts[lastname])
            print(account.getBalance())
        elif command.startswith('login'):
            [_login, new_lastname] = command.split()
            lastname = new_lastname



