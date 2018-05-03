import sys, Ice
import Bank

def welcome_message(name, lastname):
    print('*************')
    print('Hello ' + name + ' ' + lastname)
    print('*************')

port = input('enter port (default 10000): ')
if port == '':
    port = '10000'

with Ice.initialize(sys.argv) as communicator:
    base = communicator.stringToProxy("SimplePrinter:default -p " + port)
    bank = Bank.BankServicePrx.checkedCast(base)
    if not bank:
        raise RuntimeError("Invalid proxy")

    accounts = {}

    lastname = None

    while True:
        command = input('>> ')
        try:
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
                if not new_lastname in accounts.keys():
                    print('user was not created in this session')
                    continue
                lastname = new_lastname
            elif command.startswith('getCredit'):
                [_getCredit, amount] = command.split()
                account = bank.getAccount(accounts[lastname])
                print(account.getCredit(float(amount)))
        except Bank.userAlreadyExists:
            print('User already exists')
        except Bank.accountIsNotPremium:
            print('This feature is available only for premium users')
        except Bank.invalidUid:
            print('Invalid uid: maybe user does not exist or it is a bug, report it to the developers')




