import decompile




def launch():
    decompile.decompileClass(['jre/lib/rt.jar', '.'], ['com/alexkasko/interview/FibonacciTest'], None, [])

if __name__== "__main__":
    launch()
