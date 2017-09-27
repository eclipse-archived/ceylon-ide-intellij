import com.intellij.psi {
    SmartPsiElementPointer
}

shared class PsiElementGoneException()
        extends Exception("the PSI element no longer exists") {}

throws (class PsiElementGoneException)
E get<E>(SmartPsiElementPointer<out E> pointer) {
    if (exists e = pointer.element) {
        return e;
    }
    else {
        throw PsiElementGoneException();
    }
}
