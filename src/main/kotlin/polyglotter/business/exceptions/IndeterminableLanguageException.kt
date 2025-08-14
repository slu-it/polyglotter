package polyglotter.business.exceptions

import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(BAD_REQUEST)
class IndeterminableLanguageException(text: String) :
    RuntimeException("Could not determine the language of text: $text")
