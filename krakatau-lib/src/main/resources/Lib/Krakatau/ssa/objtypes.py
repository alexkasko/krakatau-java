from ..verifier import verifier_types as vtypes
from ..error import ClassLoaderError

#types are represented by classname, dimension
#primative types are .int, etc since these cannot be valid classnames since periods are forbidden
def TypeTT(baset, dim):
    assert(dim >= 0)
    return baset, dim

# Not real types
VoidTT = TypeTT('.void', 0)
NullTT = TypeTT('.null', 0)

ObjectTT = TypeTT('java/lang/Object', 0)
StringTT = TypeTT('java/lang/String', 0)
ThrowableTT = TypeTT('java/lang/Throwable', 0)
ClassTT = TypeTT('java/lang/Class', 0)

BoolTT = TypeTT('.boolean', 0)
IntTT = TypeTT('.int', 0)
LongTT = TypeTT('.long', 0)
FloatTT = TypeTT('.float', 0)
DoubleTT = TypeTT('.double', 0)

ByteTT = TypeTT('.byte', 0)
CharTT = TypeTT('.char', 0)
ShortTT = TypeTT('.short', 0)

BExpr = '.bexpr' #bool or byte

def baset(tt): return tt[0]
def dim(tt): return tt[1]
def withDimInc(tt, inc): return TypeTT(baset(tt), dim(tt)+inc)
def withNoDim(tt): return TypeTT(baset(tt), 0)

def isBaseTClass(tt): return not baset(tt).startswith('.')
def className(tt): return baset(tt) if not baset(tt).startswith('.') else None
def primName(tt): return baset(tt)[1:] if baset(tt).startswith('.') else None

###############################################################################

def isSubtype(env, x, y):
    if x == y or y == ObjectTT or x == NullTT:
        return True
    elif y == NullTT:
        return False

    xname, xdim = baset(x), dim(x)
    yname, ydim = baset(y), dim(y)
    if ydim > xdim:
        return False
    elif xdim > ydim: #TODO - these constants should be defined in one place to reduce risk of typos
        return yname in ('java/lang/Object','java/lang/Cloneable','java/io/Serializable')
    else:
        return isBaseTClass(x) and isBaseTClass(y) and env.isSubclass(xname, yname)

#Will not return interface unless all inputs are same interface or null
def commonSupertype(env, tts):
    assert(hasattr(env, 'getClass')) #catch common errors where we forget the env argument

    tts = set(tts)
    tts.discard(NullTT)

    if len(tts) == 1:
        return tts.pop()
    elif not tts:
        return NullTT

    dims = map(dim, tts)
    newdim = min(dims)
    if max(dims) > newdim or any(baset(tt) == 'java/lang/Object' for tt in tts):
        return TypeTT('java/lang/Object', newdim)
    # if any are primative arrays, result is object array of dim-1
    if not all(isBaseTClass(tt) for tt in tts):
        return TypeTT('java/lang/Object', newdim-1)

    # find common superclass of base types
    baselists = [env.getSupers(baset(tt)) for tt in tts]
    common = [x for x in zip(*baselists) if len(set(x)) == 1]
    return TypeTT(common[-1][0], newdim)

######################################################################################################
_verifierConvert = {vtypes.T_INT:IntTT, vtypes.T_FLOAT:FloatTT, vtypes.T_LONG:LongTT,
        vtypes.T_DOUBLE:DoubleTT, vtypes.T_SHORT:ShortTT, vtypes.T_CHAR:CharTT,
        vtypes.T_BYTE:ByteTT, vtypes.T_BOOL:BoolTT, vtypes.T_NULL:NullTT,
        vtypes.OBJECT_INFO:ObjectTT}

def verifierToSynthetic_seq(vtypes):
    return [verifierToSynthetic(vtype) for vtype in vtypes if not (vtype.tag and vtype.tag.endswith('2'))]

def verifierToSynthetic(vtype):
    assert(vtype.tag not in (None, '.address', '.double2', '.long2', '.new', '.init'))

    if vtype in _verifierConvert:
        return _verifierConvert[vtype]

    base = vtypes.withNoDimension(vtype)
    if base in _verifierConvert:
        return withDimInc(_verifierConvert[base], vtype.dim)

    return TypeTT(vtype.extra, vtype.dim)

#returns supers, exacts
def declTypeToActual(env, decltype):
    name, newdim = baset(decltype), dim(decltype)

    #Verifier treats bool[]s and byte[]s as interchangeable, so it could really be either
    if newdim and (name == baset(ByteTT) or name == baset(BoolTT)):
        return [], [withDimInc(ByteTT, newdim), withDimInc(BoolTT, newdim)]
    elif not isBaseTClass(decltype): #primative types can't be subclassed anyway
        return [], [decltype]

    flags = env.getFlags(name, suppressErrors=True)
    # If class is not found (returned None) assume worst case, that is a interface
    isinterface = flags is None or 'INTERFACE' in flags

    #Verifier doesn't fully verify interfaces so they could be anything
    if isinterface:
        return [withDimInc(ObjectTT, newdim)], []
    # If class is final, return it as exact, not super
    elif 'FINAL' in flags:
        return [], [decltype]
    else:
        return [decltype], []
